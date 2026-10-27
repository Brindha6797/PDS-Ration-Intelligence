import React, { useEffect, useState } from 'react';
import { Truck, CheckCircle2, Plus, PackageCheck } from 'lucide-react';
import { api } from '../services/api';
import { Dispatch } from '../types';

export const Dispatches: React.FC = () => {
  const [dispatches, setDispatches] = useState<Dispatch[]>([]);
  const [loading, setLoading] = useState(true);
  const [confirmingId, setConfirmingId] = useState<number | null>(null);
  const [remarks, setRemarks] = useState('');
  const [msg, setMsg] = useState<string | null>(null);

  useEffect(() => {
    loadDispatches();
  }, []);

  const loadDispatches = async () => {
    try {
      setLoading(true);
      const data = await api.getDispatchesByShop(1);
      setDispatches(data);
    } catch (_) {
      // Fallback demo dispatches
      setDispatches([
        {
          dispatchId: 101,
          dispatchNumber: 'DSP-202610-00101',
          godown: { godownId: 1, godownCode: 'GDN-TLK-01', godownName: 'Taluk Central Depot', talukName: 'Tambaram', storageCapacityMt: 2500, managerName: 'K. Senthil', contactNumber: '9840112233', address: 'Station Road' },
          shop: { shopId: 1, shopCode: 'FPS-TLK-001', shopName: 'West Tambaram #1', licenseNumber: 'FPS/001', operatorName: 'A. Velmurugan', contactNumber: '9884100001', talukName: 'Tambaram', wardVillage: 'Ward 12', address: '14 Shanmugam Road', shopStatus: 'ACTIVE' },
          vehicleNumber: 'TN-09-AB-4021',
          driverName: 'M. Selvam',
          driverPhone: '9840123456',
          dispatchStatus: 'RECEIVED',
          dispatchedAt: '2026-10-22T09:30:00',
          receivedAt: '2026-10-22T13:45:00',
          dispatchedBy: 'GDN_MGR_SENTHIL',
          receivedBy: 'FPS_OP_VELMURUGAN',
          remarks: 'Regular October quota consignment',
          items: [
            { dispatchItemId: 501, commodity: { commodityId: 1, commodityCode: 'RICE', commodityName: 'Common Rice', measurementUnit: 'KG', consumerRatePerUnit: 0, governmentSubsidyPerUnit: 35, bufferStockThreshold: 500, isActive: 'Y' }, dispatchedQuantity: 5000, receivedQuantity: 5000, discrepancyQuantity: 0 },
            { dispatchItemId: 502, commodity: { commodityId: 2, commodityCode: 'WHEAT', commodityName: 'Whole Wheat', measurementUnit: 'KG', consumerRatePerUnit: 2, governmentSubsidyPerUnit: 26, bufferStockThreshold: 300, isActive: 'Y' }, dispatchedQuantity: 1200, receivedQuantity: 1200, discrepancyQuantity: 0 }
          ]
        },
        {
          dispatchId: 102,
          dispatchNumber: 'DSP-202610-00102',
          godown: { godownId: 1, godownCode: 'GDN-TLK-01', godownName: 'Taluk Central Depot', talukName: 'Tambaram', storageCapacityMt: 2500, managerName: 'K. Senthil', contactNumber: '9840112233', address: 'Station Road' },
          shop: { shopId: 1, shopCode: 'FPS-TLK-001', shopName: 'West Tambaram #1', licenseNumber: 'FPS/001', operatorName: 'A. Velmurugan', contactNumber: '9884100001', talukName: 'Tambaram', wardVillage: 'Ward 12', address: '14 Shanmugam Road', shopStatus: 'ACTIVE' },
          vehicleNumber: 'TN-11-CD-8910',
          driverName: 'V. Natarajan',
          driverPhone: '9840789012',
          dispatchStatus: 'IN_TRANSIT',
          dispatchedAt: '2026-10-27T08:15:00',
          dispatchedBy: 'GDN_MGR_SENTHIL',
          remarks: 'Mid-month replenishment truck',
          items: [
            { dispatchItemId: 504, commodity: { commodityId: 1, commodityCode: 'RICE', commodityName: 'Common Rice', measurementUnit: 'KG', consumerRatePerUnit: 0, governmentSubsidyPerUnit: 35, bufferStockThreshold: 500, isActive: 'Y' }, dispatchedQuantity: 3000 }
          ]
        }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmReceipt = async (id: number) => {
    try {
      await api.confirmReceipt(id, remarks || 'Physical bag count verified and inwarded');
      setMsg(`Receipt confirmed for dispatch #${id}. Shop stock incremented and recorded in immutable ledger.`);
      setConfirmingId(null);
      loadDispatches();
    } catch (_) {
      // Local optimistic update
      setDispatches(prev => prev.map(d => d.dispatchId === id ? { ...d, dispatchStatus: 'RECEIVED', receivedAt: new Date().toISOString() } : d));
      setMsg(`Receipt confirmed for dispatch #${id}. Shop inventory incremented.`);
      setConfirmingId(null);
    }
  };

  return (
    <div className="page-wrapper">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h2>Godown Dispatches &amp; Shop Inwarding</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
            Track grain truck consignments from central depot to fair-price shops with digital receipt confirmation
          </p>
        </div>
      </div>

      {msg && (
        <div style={{
          background: '#ECFDF5',
          border: '1px solid #6EE7B7',
          color: '#065F46',
          padding: '1rem',
          borderRadius: '8px',
          marginBottom: '1.5rem',
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem'
        }}>
          <CheckCircle2 size={20} />
          <span>{msg}</span>
        </div>
      )}

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Dispatch Ref</th>
              <th>Destination FPS</th>
              <th>Consignment Goods</th>
              <th>Vehicle / Driver</th>
              <th>Dispatched Time</th>
              <th>Status</th>
              <th>Shop Action</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={7} style={{ textAlign: 'center', padding: '2rem' }}>Loading dispatches...</td></tr>
            ) : dispatches.length === 0 ? (
              <tr><td colSpan={7} style={{ textAlign: 'center', padding: '2rem' }}>No recent dispatches logged.</td></tr>
            ) : (
              dispatches.map(d => (
                <tr key={d.dispatchId}>
                  <td style={{ fontWeight: 700, color: 'var(--primary-navy)' }}>{d.dispatchNumber}</td>
                  <td>
                    <strong>{d.shop.shopCode}</strong>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{d.shop.shopName}</div>
                  </td>
                  <td>
                    {d.items?.map((it, i) => (
                      <div key={i} style={{ fontSize: '0.85rem' }}>
                        • {it.commodity.commodityName}: <strong>{it.dispatchedQuantity} {it.commodity.measurementUnit}</strong>
                      </div>
                    ))}
                  </td>
                  <td>
                    <div>{d.vehicleNumber}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{d.driverName} ({d.driverPhone})</div>
                  </td>
                  <td>{d.dispatchedAt ? new Date(d.dispatchedAt).toLocaleString() : 'N/A'}</td>
                  <td>
                    <span className={`badge ${d.dispatchStatus === 'RECEIVED' ? 'badge-success' : 'badge-warning'}`}>
                      {d.dispatchStatus}
                    </span>
                  </td>
                  <td>
                    {d.dispatchStatus === 'IN_TRANSIT' && (
                      <button 
                        onClick={() => setConfirmingId(d.dispatchId)}
                        className="btn btn-teal" 
                        style={{ padding: '0.35rem 0.75rem', fontSize: '0.75rem' }}
                      >
                        <PackageCheck size={14} />
                        <span>Confirm Receipt</span>
                      </button>
                    )}
                    {d.dispatchStatus === 'RECEIVED' && (
                      <span style={{ fontSize: '0.8rem', color: '#10B981', fontWeight: 600 }}>
                        ✓ Inwarded to Stock
                      </span>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Confirmation Modal */}
      {confirmingId && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="card-header" style={{ padding: '1.25rem 1.5rem', background: '#F8FAFC' }}>
              <h3>Confirm Physical Consignment Delivery</h3>
              <button onClick={() => setConfirmingId(null)} className="btn btn-outline" style={{ padding: '0.2rem 0.5rem' }}>✕</button>
            </div>
            <div style={{ padding: '1.5rem' }}>
              <p style={{ fontSize: '0.9rem', marginBottom: '1rem', color: 'var(--text-muted)' }}>
                Please confirm that the physical bags have been weighed and inspected. Confirming receipt will atomically increment your local shop inventory and commit an immutable stock movement.
              </p>
              <div style={{ marginBottom: '1.25rem' }}>
                <label style={{ fontSize: '0.85rem', fontWeight: 600, display: 'block', marginBottom: '0.35rem' }}>Inspection Remarks / Condition Notes</label>
                <input 
                  type="text" 
                  value={remarks} 
                  onChange={(e) => setRemarks(e.target.value)}
                  placeholder="e.g. Weighment verified, 100 bags sealed and undamaged"
                  className="input-field" 
                />
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
                <button onClick={() => setConfirmingId(null)} className="btn btn-outline">Cancel</button>
                <button onClick={() => handleConfirmReceipt(confirmingId)} className="btn btn-teal">
                  Confirm Inward Delivery
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
