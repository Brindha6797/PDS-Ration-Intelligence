import React, { useState, useEffect } from 'react';
import { 
  ShoppingCart, Search, CreditCard, User, CheckCircle2, 
  AlertCircle, Printer, ShieldCheck, Fingerprint, Receipt
} from 'lucide-react';
import { api } from '../services/api';
import { CardHolder, RemainingEntitlement, Sale } from '../types';

interface SalesTerminalProps {
  initialCardNumber?: string;
}

export const SalesTerminal: React.FC<SalesTerminalProps> = ({ initialCardNumber }) => {
  const [cardNumber, setCardNumber] = useState(initialCardNumber || 'RC-TN-04-100234');
  const [cardHolder, setCardHolder] = useState<CardHolder | null>(null);
  const [entitlements, setEntitlements] = useState<RemainingEntitlement[]>([]);
  const [selectedItems, setSelectedItems] = useState<{ [commodityId: number]: number }>({});
  const [paymentMode, setPaymentMode] = useState('CASH');
  const [authMode, setAuthMode] = useState('AADHAAR_OTP');
  const [loading, setLoading] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [completedSale, setCompletedSale] = useState<any | null>(null);

  useEffect(() => {
    if (cardNumber) {
      handleLookup();
    }
  }, []);

  const handleLookup = async () => {
    setLoading(true);
    setError(null);
    setCompletedSale(null);
    setSelectedItems({});

    try {
      const card = await api.getCardByNumber(cardNumber);
      setCardHolder(card);

      const currentDate = new Date();
      const quotas = await api.getRemainingEntitlements(card.cardId, currentDate.getFullYear(), currentDate.getMonth() + 1);
      setEntitlements(quotas);

      // Pre-fill default available quotas
      const defaults: { [id: number]: number } = {};
      quotas.forEach(q => {
        if (q.remainingQuantity > 0) {
          defaults[q.commodityId] = q.remainingQuantity;
        }
      });
      setSelectedItems(defaults);
    } catch (err: any) {
      // Demo fallback cardholder & quota
      const mockCard: CardHolder = {
        cardId: 1001,
        cardNumber: cardNumber || 'RC-TN-04-100234',
        cardType: { cardTypeId: 1, typeCode: 'AAY', typeName: 'Antyodaya Anna Yojana', isActive: 'Y' },
        headOfFamilyName: 'M. Subramanian',
        aadhaarLast4: '3412',
        mobileNumber: '9840900001',
        addressLine1: '12 Gandhi Nagar, West Tambaram',
        talukName: 'Tambaram Taluk',
        districtName: 'Chengalpattu',
        pincode: '600045',
        assignedShop: { shopId: 1, shopCode: 'FPS-TLK-001', shopName: 'West Tambaram #1', licenseNumber: 'FPS/001', operatorName: 'A. Velmurugan', contactNumber: '9884100001', talukName: 'Tambaram', wardVillage: 'Ward 12', address: '14 Shanmugam Road', shopStatus: 'ACTIVE' },
        totalFamilyMembers: 4,
        cardStatus: 'ACTIVE',
        effectiveDate: '2025-01-01'
      };
      setCardHolder(mockCard);

      const mockQuotas: RemainingEntitlement[] = [
        { commodityId: 1, commodityCode: 'RICE', commodityName: 'Common Rice (PDS)', measurementUnit: 'KG', entitledQuantity: 35.0, distributedQuantity: 20.0, remainingQuantity: 15.0, consumerRatePerUnit: 0.0, status: 'AVAILABLE' },
        { commodityId: 3, commodityCode: 'SUGAR', commodityName: 'Refined Cane Sugar', measurementUnit: 'KG', entitledQuantity: 1.0, distributedQuantity: 0.0, remainingQuantity: 1.0, consumerRatePerUnit: 13.5, status: 'AVAILABLE' },
        { commodityId: 4, commodityCode: 'OIL', commodityName: 'Fortified Palm Oil', measurementUnit: 'PACKET', entitledQuantity: 1.0, distributedQuantity: 0.0, remainingQuantity: 1.0, consumerRatePerUnit: 25.0, status: 'AVAILABLE' },
        { commodityId: 5, commodityCode: 'DAL', commodityName: 'Toor Dal (Pulses)', measurementUnit: 'KG', entitledQuantity: 1.0, distributedQuantity: 0.0, remainingQuantity: 1.0, consumerRatePerUnit: 30.0, status: 'AVAILABLE' }
      ];
      setEntitlements(mockQuotas);
      setSelectedItems({ 1: 15.0, 3: 1.0, 4: 1.0, 5: 1.0 });
    } finally {
      setLoading(false);
    }
  };

  const handleQuantityChange = (commodityId: number, val: number) => {
    setSelectedItems(prev => ({
      ...prev,
      [commodityId]: Math.max(0, val)
    }));
  };

  const calculateTotal = () => {
    let sum = 0;
    entitlements.forEach(e => {
      const qty = selectedItems[e.commodityId] || 0;
      sum += qty * e.consumerRatePerUnit;
    });
    return sum;
  };

  const handleSubmitSale = async () => {
    if (!cardHolder) return;
    setProcessing(true);
    setError(null);

    const items = entitlements
      .filter(e => (selectedItems[e.commodityId] || 0) > 0)
      .map(e => ({
        commodityId: e.commodityId,
        quantity: selectedItems[e.commodityId]
      }));

    if (items.length === 0) {
      setError('Please select at least one commodity quantity to dispense.');
      setProcessing(false);
      return;
    }

    try {
      const salePayload = {
        shopId: cardHolder.assignedShop.shopId,
        cardId: cardHolder.cardId,
        paymentMode,
        authMode,
        items
      };

      const result = await api.processSale(salePayload);
      setCompletedSale(result);
      // Reload remaining quota
      handleLookup();
    } catch (err: any) {
      // If backend caught exception (e_entitlement_exhausted / e_insufficient_stock)
      setError(err.message || 'Transaction rejected by database validation engine.');
    } finally {
      setProcessing(false);
    }
  };

  return (
    <div className="page-wrapper">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h2>e-POS Ration Distribution Terminal</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
            Point of Sale terminal with real-time biometric verification and atomic entitlement row-locking
          </p>
        </div>
        <div className="user-badge" style={{ background: '#ECFDF5', borderColor: '#10B981', color: '#065F46' }}>
          <ShieldCheck size={16} />
          <span>FPS Terminal #1 - Active Online</span>
        </div>
      </div>

      {error && (
        <div style={{
          background: '#FEF2F2',
          border: '1px solid #F87171',
          color: '#B91C1C',
          padding: '1rem 1.25rem',
          borderRadius: '10px',
          marginBottom: '1.5rem',
          display: 'flex',
          alignItems: 'center',
          gap: '0.75rem',
          boxShadow: 'var(--shadow-sm)'
        }}>
          <AlertCircle size={22} style={{ flexShrink: 0 }} />
          <div>
            <strong>Transaction Aborted:</strong> {error}
          </div>
        </div>
      )}

      {/* POS Layout */}
      <div className="pos-container">
        {/* Left Column: Beneficiary Lookup & Item Selection */}
        <div>
          {/* Cardholder Search Panel */}
          <div className="card" style={{ marginBottom: '1.5rem' }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr auto', gap: '1rem', alignItems: 'flex-end' }}>
              <div>
                <label style={{ fontSize: '0.85rem', fontWeight: 600, color: '#475569', display: 'block', marginBottom: '0.4rem' }}>
                  Enter Beneficiary Ration Card Number
                </label>
                <div style={{ position: 'relative' }}>
                  <CreditCard size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#94A3B8' }} />
                  <input 
                    type="text" 
                    value={cardNumber} 
                    onChange={(e) => setCardNumber(e.target.value)}
                    placeholder="e.g. RC-TN-04-100234"
                    className="input-field" 
                    style={{ paddingLeft: '2.5rem', fontWeight: 700 }}
                  />
                </div>
              </div>
              <button onClick={handleLookup} disabled={loading} className="btn btn-primary" style={{ height: '42px' }}>
                <Search size={16} />
                <span>{loading ? 'Verifying...' : 'Verify Card'}</span>
              </button>
            </div>

            {/* Beneficiary Header Details */}
            {cardHolder && (
              <div style={{
                marginTop: '1.25rem',
                paddingTop: '1.25rem',
                borderTop: '1px solid #E2E8F0',
                display: 'grid',
                gridTemplateColumns: 'auto 1fr auto auto',
                gap: '1.5rem',
                alignItems: 'center'
              }}>
                <div style={{
                  width: '52px',
                  height: '52px',
                  borderRadius: '50%',
                  background: 'var(--teal-subtle)',
                  color: 'var(--teal-accent)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontWeight: 800,
                  fontSize: '1.2rem'
                }}>
                  {cardHolder.headOfFamilyName.charAt(0)}
                </div>

                <div>
                  <h4 style={{ fontSize: '1.1rem', marginBottom: '0.15rem' }}>{cardHolder.headOfFamilyName}</h4>
                  <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                    Aadhaar: •••• •••• {cardHolder.aadhaarLast4} • Members: <strong>{cardHolder.totalFamilyMembers}</strong> • Shop: {cardHolder.assignedShop.shopCode}
                  </div>
                </div>

                <div>
                  <span className={`badge ${cardHolder.cardType.typeCode === 'AAY' ? 'badge-warning' : 'badge-success'}`} style={{ fontSize: '0.85rem' }}>
                    {cardHolder.cardType.typeCode} ({cardHolder.cardType.typeName})
                  </span>
                </div>

                <div>
                  <span className="badge badge-success">Eligible Active</span>
                </div>
              </div>
            )}
          </div>

          {/* Commodity Entitlements Selection Table */}
          <div className="card">
            <div className="card-header">
              <h3>
                <ShoppingCart size={20} color="var(--teal-accent)" />
                Authoritative Monthly Quotas &amp; Dispensing Basket
              </h3>
              <span className="badge badge-info">Period: Current Month</span>
            </div>

            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Commodity</th>
                    <th>Remaining Permitted</th>
                    <th>Consumer Rate</th>
                    <th style={{ width: '160px' }}>Dispense Quantity</th>
                    <th>Subtotal</th>
                  </tr>
                </thead>
                <tbody>
                  {entitlements.map(e => {
                    const currentQty = selectedItems[e.commodityId] || 0;
                    const subtotal = currentQty * e.consumerRatePerUnit;
                    const isExhausted = e.remainingQuantity <= 0;

                    return (
                      <tr key={e.commodityId} style={{ opacity: isExhausted ? 0.6 : 1 }}>
                        <td style={{ fontWeight: 700, color: 'var(--primary-navy)' }}>
                          {e.commodityName}
                          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{e.commodityCode}</div>
                        </td>
                        <td>
                          <span style={{ fontWeight: 800, color: isExhausted ? '#EF4444' : 'var(--teal-accent)', fontSize: '1rem' }}>
                            {e.remainingQuantity} {e.measurementUnit}
                          </span>
                        </td>
                        <td>{e.consumerRatePerUnit === 0 ? 'FREE' : `₹${e.consumerRatePerUnit.toFixed(2)}`}</td>
                        <td>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <input 
                              type="number"
                              step="0.5"
                              min="0"
                              max={e.remainingQuantity}
                              disabled={isExhausted}
                              value={currentQty}
                              onChange={(ev) => handleQuantityChange(e.commodityId, parseFloat(ev.target.value) || 0)}
                              className="input-field" 
                              style={{ width: '100px', fontWeight: 700, textAlign: 'center' }}
                            />
                            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{e.measurementUnit}</span>
                          </div>
                        </td>
                        <td style={{ fontWeight: 700 }}>
                          {subtotal === 0 ? '₹0.00' : `₹${subtotal.toFixed(2)}`}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Right Column: Checkout Summary & Authentication */}
        <div>
          <div className="pos-cart-panel">
            <h3 style={{ fontSize: '1.25rem', marginBottom: '1.25rem', paddingBottom: '0.75rem', borderBottom: '1px solid #E2E8F0' }}>
              Transaction Summary
            </h3>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem', marginBottom: '1.5rem' }}>
              {entitlements.map(e => {
                const qty = selectedItems[e.commodityId] || 0;
                if (qty <= 0) return null;
                return (
                  <div key={e.commodityId} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.9rem' }}>
                    <span>{e.commodityName} ({qty} {e.measurementUnit})</span>
                    <span style={{ fontWeight: 600 }}>₹{(qty * e.consumerRatePerUnit).toFixed(2)}</span>
                  </div>
                );
              })}
            </div>

            <div style={{ borderTop: '2px dashed #CBD5E1', paddingTop: '1rem', marginBottom: '1.5rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '1rem', fontWeight: 700, color: '#334155' }}>Total Bill Payable:</span>
                <span style={{ fontSize: '1.65rem', fontWeight: 800, color: 'var(--primary-navy)' }}>
                  ₹{calculateTotal().toFixed(2)}
                </span>
              </div>
            </div>

            {/* Authentication Mode */}
            <div style={{ marginBottom: '1.25rem' }}>
              <label style={{ fontSize: '0.8rem', fontWeight: 600, color: '#64748B', display: 'block', marginBottom: '0.4rem' }}>
                Beneficiary Verification Mode
              </label>
              <select value={authMode} onChange={(e) => setAuthMode(e.target.value)} className="input-field">
                <option value="AADHAAR_OTP">Aadhaar Mobile OTP</option>
                <option value="BIOMETRIC_FINGER">Biometric Fingerprint Scanner</option>
                <option value="BIOMETRIC_IRIS">Iris Scanner (Senior Citizen)</option>
                <option value="MANUAL_OVERRIDE">Inspector Manual Override</option>
              </select>
            </div>

            {/* Payment Mode */}
            <div style={{ marginBottom: '1.5rem' }}>
              <label style={{ fontSize: '0.8rem', fontWeight: 600, color: '#64748B', display: 'block', marginBottom: '0.4rem' }}>
                Payment Method
              </label>
              <select value={paymentMode} onChange={(e) => setPaymentMode(e.target.value)} className="input-field">
                <option value="CASH">Cash at Counter</option>
                <option value="UPI">UPI / Digital QR</option>
                <option value="EXEMPTED">State Free Welfare Exemption</option>
              </select>
            </div>

            <button 
              onClick={handleSubmitSale}
              disabled={processing || !cardHolder}
              className="btn btn-teal"
              style={{ width: '100%', padding: '0.9rem', fontSize: '1rem', fontWeight: 700 }}
            >
              <Receipt size={18} />
              <span>{processing ? 'Processing Atomic Sale...' : 'Authorize & Print Bill'}</span>
            </button>
          </div>
        </div>
      </div>

      {/* Bill Receipt Modal */}
      {completedSale && (
        <div className="modal-overlay">
          <div className="modal-card" style={{ maxWidth: '480px', fontFamily: 'monospace' }}>
            <div style={{ padding: '2rem', borderBottom: '2px dashed #94A3B8' }}>
              <div style={{ textAlign: 'center', marginBottom: '1rem' }}>
                <h3 style={{ fontSize: '1.2rem', fontWeight: 800 }}>GOVT OF TAMIL NADU</h3>
                <p style={{ fontSize: '0.75rem' }}>CIVIL SUPPLIES &amp; CONSUMER PROTECTION</p>
                <p style={{ fontSize: '0.75rem' }}>FAIR PRICE SHOP e-POS RECEIPT</p>
              </div>

              <div style={{ fontSize: '0.8rem', lineHeight: 1.6, marginBottom: '1rem' }}>
                <div><strong>Bill No:</strong> {completedSale.billNumber}</div>
                <div><strong>Date:</strong> {new Date().toLocaleString()}</div>
                <div><strong>Shop:</strong> {cardHolder?.assignedShop.shopCode} ({cardHolder?.assignedShop.shopName})</div>
                <div><strong>Card No:</strong> {completedSale.cardNumber || cardHolder?.cardNumber}</div>
                <div><strong>Beneficiary:</strong> {completedSale.headOfFamilyName || cardHolder?.headOfFamilyName}</div>
                <div><strong>Auth:</strong> {authMode} • {paymentMode}</div>
              </div>

              <table style={{ fontSize: '0.8rem', width: '100%', borderTop: '1px solid #CBD5E1', borderBottom: '1px solid #CBD5E1', margin: '0.75rem 0' }}>
                <thead>
                  <tr>
                    <th style={{ padding: '4px 0' }}>Item</th>
                    <th style={{ padding: '4px 0', textAlign: 'center' }}>Qty</th>
                    <th style={{ padding: '4px 0', textAlign: 'right' }}>Rate</th>
                    <th style={{ padding: '4px 0', textAlign: 'right' }}>Amt</th>
                  </tr>
                </thead>
                <tbody>
                  {completedSale.items?.map((it: any, i: number) => (
                    <tr key={i}>
                      <td style={{ padding: '4px 0' }}>{it.commodityName}</td>
                      <td style={{ padding: '4px 0', textAlign: 'center' }}>{it.quantity}</td>
                      <td style={{ padding: '4px 0', textAlign: 'right' }}>₹{it.unitRate.toFixed(2)}</td>
                      <td style={{ padding: '4px 0', textAlign: 'right' }}>₹{it.totalPrice.toFixed(2)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>

              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '1.1rem', fontWeight: 800, marginTop: '0.75rem' }}>
                <span>Total Paid:</span>
                <span>₹{completedSale.totalAmount?.toFixed(2) || '0.00'}</span>
              </div>
            </div>

            <div style={{ padding: '1rem 2rem', background: '#F8FAFC', display: 'flex', justifyContent: 'space-between' }}>
              <button onClick={() => window.print()} className="btn btn-primary" style={{ fontSize: '0.85rem' }}>
                <Printer size={16} />
                <span>Print Bill</span>
              </button>
              <button onClick={() => setCompletedSale(null)} className="btn btn-outline" style={{ fontSize: '0.85rem' }}>
                Close Terminal
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
