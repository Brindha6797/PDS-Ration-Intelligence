import React, { useEffect, useState } from 'react';
import { Search, Filter, CreditCard, Users, AlertCircle, Eye } from 'lucide-react';
import { api } from '../services/api';
import { CardHolder, Shop } from '../types';

interface CardHoldersProps {
  onSelectCardForPos?: (cardNumber: string) => void;
}

export const CardHolders: React.FC<CardHoldersProps> = ({ onSelectCardForPos }) => {
  const [cards, setCards] = useState<CardHolder[]>([]);
  const [shops, setShops] = useState<Shop[]>([]);
  const [search, setSearch] = useState('');
  const [selectedShop, setSelectedShop] = useState<number | undefined>();
  const [showNonDrawalOnly, setShowNonDrawalOnly] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadShops();
    loadCards();
  }, [search, selectedShop, showNonDrawalOnly]);

  const loadShops = async () => {
    try {
      const data = await api.getShops();
      setShops(data);
    } catch (_) {}
  };

  const loadCards = async () => {
    try {
      setLoading(true);
      if (showNonDrawalOnly) {
        const data = await api.getNonDrawalReport();
        setCards(data);
      } else {
        const data = await api.getCards(0, 50, search, selectedShop);
        setCards(data.content || data);
      }
    } catch (_) {
      // Fallback demo cards
      setCards([
        {
          cardId: 1001,
          cardNumber: 'RC-TN-04-100234',
          cardType: { cardTypeId: 1, typeCode: 'AAY', typeName: 'Antyodaya Anna Yojana', isActive: 'Y', colorCode: 'YELLOW' },
          headOfFamilyName: 'M. Subramanian',
          aadhaarLast4: '3412',
          mobileNumber: '9840900001',
          addressLine1: '12 Gandhi Nagar, West Tambaram',
          talukName: 'Tambaram Taluk',
          districtName: 'Chengalpattu',
          pincode: '600045',
          assignedShop: { shopId: 1, shopCode: 'FPS-TLK-001', shopName: 'West Tambaram #1', licenseNumber: 'FPS/001', operatorName: 'A. Velmurugan', contactNumber: '9884100001', talukName: 'Tambaram', wardVillage: 'Ward 12', address: '14 Shanmugam Road', shopStatus: 'ACTIVE' },
          totalFamilyMembers: 4,
          annualIncome: 36000,
          cardStatus: 'ACTIVE',
          effectiveDate: '2025-01-01'
        },
        {
          cardId: 1002,
          cardNumber: 'RC-TN-04-100235',
          cardType: { cardTypeId: 2, typeCode: 'PHH', typeName: 'Priority Household', isActive: 'Y', colorCode: 'PINK' },
          headOfFamilyName: 'K. Revathi',
          aadhaarLast4: '7890',
          mobileNumber: '9840900002',
          addressLine1: '45 Station Road, West Tambaram',
          talukName: 'Tambaram Taluk',
          districtName: 'Chengalpattu',
          pincode: '600045',
          assignedShop: { shopId: 1, shopCode: 'FPS-TLK-001', shopName: 'West Tambaram #1', licenseNumber: 'FPS/001', operatorName: 'A. Velmurugan', contactNumber: '9884100001', talukName: 'Tambaram', wardVillage: 'Ward 12', address: '14 Shanmugam Road', shopStatus: 'ACTIVE' },
          totalFamilyMembers: 3,
          annualIncome: 72000,
          cardStatus: 'ACTIVE',
          effectiveDate: '2025-01-01'
        },
        {
          cardId: 1003,
          cardNumber: 'RC-TN-04-100236',
          cardType: { cardTypeId: 3, typeCode: 'NPHH', typeName: 'Non-Priority Household', isActive: 'Y', colorCode: 'WHITE' },
          headOfFamilyName: 'S. Vijayakumar',
          aadhaarLast4: '1122',
          mobileNumber: '9840900003',
          addressLine1: '78 Rajaji Street, West Tambaram',
          talukName: 'Tambaram Taluk',
          districtName: 'Chengalpattu',
          pincode: '600045',
          assignedShop: { shopId: 1, shopCode: 'FPS-TLK-001', shopName: 'West Tambaram #1', licenseNumber: 'FPS/001', operatorName: 'A. Velmurugan', contactNumber: '9884100001', talukName: 'Tambaram', wardVillage: 'Ward 12', address: '14 Shanmugam Road', shopStatus: 'ACTIVE' },
          totalFamilyMembers: 4,
          annualIncome: 180000,
          cardStatus: 'ACTIVE',
          effectiveDate: '2025-01-01'
        },
        {
          cardId: 1004,
          cardNumber: 'RC-TN-04-100237',
          cardType: { cardTypeId: 4, typeCode: 'AY', typeName: 'Annapurna Yojana', isActive: 'Y', colorCode: 'GREEN' },
          headOfFamilyName: 'P. Meenakshi Ammal',
          aadhaarLast4: '5566',
          mobileNumber: '9840900004',
          addressLine1: '3 Temple Street, West Tambaram',
          talukName: 'Tambaram Taluk',
          districtName: 'Chengalpattu',
          pincode: '600045',
          assignedShop: { shopId: 1, shopCode: 'FPS-TLK-001', shopName: 'West Tambaram #1', licenseNumber: 'FPS/001', operatorName: 'A. Velmurugan', contactNumber: '9884100001', talukName: 'Tambaram', wardVillage: 'Ward 12', address: '14 Shanmugam Road', shopStatus: 'ACTIVE' },
          totalFamilyMembers: 1,
          annualIncome: 12000,
          cardStatus: 'ACTIVE',
          effectiveDate: '2025-01-01'
        }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const getCategoryBadgeClass = (code: string) => {
    switch (code) {
      case 'AAY': return 'badge-warning';
      case 'PHH': return 'badge-success';
      case 'NPHH': return 'badge-info';
      default: return 'badge-success';
    }
  };

  return (
    <div className="page-wrapper">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h2>Ration Cardholder Registry</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
            Beneficiary database management, household quotas, and biometric linkages
          </p>
        </div>
        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <button 
            onClick={() => setShowNonDrawalOnly(!showNonDrawalOnly)}
            className={`btn ${showNonDrawalOnly ? 'btn-teal' : 'btn-outline'}`}
          >
            <AlertCircle size={16} />
            <span>{showNonDrawalOnly ? 'Showing 3-Month Non-Drawals' : 'Audit 3-Month Non-Drawals'}</span>
          </button>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="card" style={{ marginBottom: '1.5rem', padding: '1rem 1.5rem' }}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 280px auto', gap: '1rem', alignItems: 'center' }}>
          <div style={{ position: 'relative' }}>
            <Search size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#94A3B8' }} />
            <input 
              type="text" 
              placeholder="Search by ration card number or head of family name..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="input-field" 
              style={{ paddingLeft: '2.5rem' }}
            />
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Filter size={18} color="var(--text-muted)" />
            <select 
              value={selectedShop || ''} 
              onChange={(e) => setSelectedShop(e.target.value ? Number(e.target.value) : undefined)}
              className="input-field"
            >
              <option value="">All Fair-Price Shops</option>
              {shops.map(s => (
                <option key={s.shopId} value={s.shopId}>{s.shopCode} - {s.shopName}</option>
              ))}
            </select>
          </div>

          <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', whiteSpace: 'nowrap' }}>
            Showing <strong>{cards.length}</strong> cardholders
          </span>
        </div>
      </div>

      {/* Cardholders Table */}
      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Ration Card No</th>
              <th>Category</th>
              <th>Head of Family</th>
              <th>Aadhaar</th>
              <th>Members</th>
              <th>Assigned FPS</th>
              <th>Annual Income</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={9} style={{ textAlign: 'center', padding: '2rem' }}>Loading cardholders...</td></tr>
            ) : cards.length === 0 ? (
              <tr><td colSpan={9} style={{ textAlign: 'center', padding: '2rem' }}>No ration cards found matching criteria.</td></tr>
            ) : (
              cards.map((c) => (
                <tr key={c.cardId}>
                  <td style={{ fontWeight: 700, color: 'var(--primary-navy)' }}>
                    {c.cardNumber}
                  </td>
                  <td>
                    <span className={`badge ${getCategoryBadgeClass(c.cardType.typeCode)}`}>
                      {c.cardType.typeCode}
                    </span>
                  </td>
                  <td style={{ fontWeight: 600 }}>{c.headOfFamilyName}</td>
                  <td>•••• •••• {c.aadhaarLast4}</td>
                  <td>
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.35rem' }}>
                      <Users size={14} /> {c.totalFamilyMembers}
                    </span>
                  </td>
                  <td>{c.assignedShop.shopCode}</td>
                  <td>₹{c.annualIncome?.toLocaleString() || 'N/A'}</td>
                  <td>
                    <span className={`badge ${c.cardStatus === 'ACTIVE' ? 'badge-success' : 'badge-danger'}`}>
                      {c.cardStatus}
                    </span>
                  </td>
                  <td>
                    {onSelectCardForPos && (
                      <button 
                        onClick={() => onSelectCardForPos(c.cardNumber)}
                        className="btn btn-outline" 
                        style={{ padding: '0.3rem 0.65rem', fontSize: '0.75rem' }}
                      >
                        POS Sell
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
