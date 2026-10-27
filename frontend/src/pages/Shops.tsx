import React, { useEffect, useState } from 'react';
import { Store, Eye, Search, MapPin, Phone, Package } from 'lucide-react';
import { api } from '../services/api';
import { Shop, ShopStock } from '../types';

export const Shops: React.FC = () => {
  const [shops, setShops] = useState<Shop[]>([]);
  const [selectedShopStock, setSelectedShopStock] = useState<{ shop: Shop; stock: ShopStock[] } | null>(null);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadShops();
  }, []);

  const loadShops = async () => {
    try {
      setLoading(true);
      const data = await api.getShops();
      setShops(data);
    } catch (_) {
      // Fallback demo shops
      setShops([
        { shopId: 1, shopCode: 'FPS-TLK-001', shopName: 'Fair Price Shop #1 - West Tambaram', licenseNumber: 'FPS/LIC/TN/001', operatorName: 'A. Velmurugan', contactNumber: '9884100001', talukName: 'Tambaram Taluk', wardVillage: 'Ward 12', address: '14 Shanmugam Road, West Tambaram', shopStatus: 'ACTIVE' },
        { shopId: 2, shopCode: 'FPS-TLK-002', shopName: 'Fair Price Shop #2 - East Tambaram', licenseNumber: 'FPS/LIC/TN/002', operatorName: 'M. Saravanan', contactNumber: '9884100002', talukName: 'Tambaram Taluk', wardVillage: 'Ward 15', address: '22 Rajaji Salai, East Tambaram', shopStatus: 'ACTIVE' },
        { shopId: 3, shopCode: 'FPS-TLK-003', shopName: 'Fair Price Shop #3 - Chromepet Main', licenseNumber: 'FPS/LIC/TN/003', operatorName: 'V. Elango', contactNumber: '9884100003', talukName: 'Tambaram Taluk', wardVillage: 'Ward 21', address: '8 Radhanagar Main Road, Chromepet', shopStatus: 'ACTIVE' },
        { shopId: 4, shopCode: 'FPS-TLK-004', shopName: 'Fair Price Shop #4 - Sanatorium Colony', licenseNumber: 'FPS/LIC/TN/004', operatorName: 'K. Deepa', contactNumber: '9884100004', talukName: 'Tambaram Taluk', wardVillage: 'Ward 18', address: '5 Gandhi Street, Tambaram Sanatorium', shopStatus: 'ACTIVE' },
        { shopId: 5, shopCode: 'FPS-TLK-005', shopName: 'Fair Price Shop #5 - Mudichur Village', licenseNumber: 'FPS/LIC/TN/005', operatorName: 'R. Ganesan', contactNumber: '9884100005', talukName: 'Tambaram Taluk', wardVillage: 'Mudichur Rural', address: '19 High Road, Mudichur', shopStatus: 'ACTIVE' }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleInspectStock = async (shop: Shop) => {
    try {
      const stock = await api.getShopStock(shop.shopId);
      setSelectedShopStock({ shop, stock });
    } catch (_) {
      // Fallback stock
      setSelectedShopStock({
        shop,
        stock: [
          { stockId: 1, shop, commodity: { commodityId: 1, commodityCode: 'RICE', commodityName: 'Common Rice', measurementUnit: 'KG', consumerRatePerUnit: 0, governmentSubsidyPerUnit: 35, bufferStockThreshold: 500, isActive: 'Y' }, currentQuantity: 4850, openingMonthBalance: 1200, totalReceivedMonth: 5000, totalDistributedMonth: 1350 },
          { stockId: 2, shop, commodity: { commodityId: 2, commodityCode: 'WHEAT', commodityName: 'Whole Wheat', measurementUnit: 'KG', consumerRatePerUnit: 2, governmentSubsidyPerUnit: 26, bufferStockThreshold: 300, isActive: 'Y' }, currentQuantity: 1200, openingMonthBalance: 300, totalReceivedMonth: 1200, totalDistributedMonth: 300 },
          { stockId: 3, shop, commodity: { commodityId: 3, commodityCode: 'SUGAR', commodityName: 'Cane Sugar', measurementUnit: 'KG', consumerRatePerUnit: 13.5, governmentSubsidyPerUnit: 24, bufferStockThreshold: 150, isActive: 'Y' }, currentQuantity: 420, openingMonthBalance: 100, totalReceivedMonth: 450, totalDistributedMonth: 130 }
        ]
      });
    }
  };

  const filteredShops = shops.filter(s => 
    s.shopName.toLowerCase().includes(search.toLowerCase()) ||
    s.shopCode.toLowerCase().includes(search.toLowerCase()) ||
    s.wardVillage.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="page-wrapper">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h2>Fair-Price Shop (FPS) Network</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
            120 authorized ration distribution centers in Tambaram Taluk
          </p>
        </div>
      </div>

      <div className="card" style={{ marginBottom: '1.5rem', padding: '1rem 1.5rem' }}>
        <div style={{ position: 'relative', maxWidth: '400px' }}>
          <Search size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#94A3B8' }} />
          <input 
            type="text" 
            placeholder="Search shops by code, name, or ward..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="input-field" 
            style={{ paddingLeft: '2.5rem' }}
          />
        </div>
      </div>

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Shop Code</th>
              <th>Shop Name</th>
              <th>License No</th>
              <th>Operator</th>
              <th>Contact</th>
              <th>Ward / Village</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={8} style={{ textAlign: 'center', padding: '2rem' }}>Loading shop network...</td></tr>
            ) : (
              filteredShops.map((s) => (
                <tr key={s.shopId}>
                  <td style={{ fontWeight: 700, color: 'var(--primary-navy)' }}>{s.shopCode}</td>
                  <td style={{ fontWeight: 600 }}>{s.shopName}</td>
                  <td>{s.licenseNumber}</td>
                  <td>{s.operatorName}</td>
                  <td>
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.35rem' }}>
                      <Phone size={13} color="var(--text-muted)" /> {s.contactNumber}
                    </span>
                  </td>
                  <td>{s.wardVillage}</td>
                  <td>
                    <span className={`badge ${s.shopStatus === 'ACTIVE' ? 'badge-success' : 'badge-danger'}`}>
                      {s.shopStatus}
                    </span>
                  </td>
                  <td>
                    <button 
                      onClick={() => handleInspectStock(s)}
                      className="btn btn-outline" 
                      style={{ padding: '0.35rem 0.75rem', fontSize: '0.75rem' }}
                    >
                      <Eye size={14} />
                      <span>Live Stock</span>
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Live Shop Stock Modal */}
      {selectedShopStock && (
        <div className="modal-overlay">
          <div className="modal-card" style={{ maxWidth: '750px' }}>
            <div className="card-header" style={{ padding: '1.25rem 1.5rem', background: '#F8FAFC' }}>
              <div>
                <h3 style={{ fontSize: '1.15rem' }}>
                  <Store size={20} color="var(--teal-accent)" />
                  {selectedShopStock.shop.shopCode} • {selectedShopStock.shop.shopName}
                </h3>
                <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                  {selectedShopStock.shop.address} • Operator: {selectedShopStock.shop.operatorName}
                </p>
              </div>
              <button 
                onClick={() => setSelectedShopStock(null)} 
                className="btn btn-outline" 
                style={{ padding: '0.25rem 0.65rem' }}
              >
                ✕ Close
              </button>
            </div>

            <div style={{ padding: '1.5rem' }}>
              <table style={{ border: '1px solid #E2E8F0', borderRadius: '8px' }}>
                <thead>
                  <tr>
                    <th>Commodity</th>
                    <th>Opening</th>
                    <th>Received</th>
                    <th>Sold (MTD)</th>
                    <th>Current Inventory</th>
                    <th>Buffer Health</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedShopStock.stock.map(stk => (
                    <tr key={stk.stockId}>
                      <td style={{ fontWeight: 700 }}>{stk.commodity.commodityName}</td>
                      <td>{stk.openingMonthBalance} {stk.commodity.measurementUnit}</td>
                      <td>+{stk.totalReceivedMonth} {stk.commodity.measurementUnit}</td>
                      <td>-{stk.totalDistributedMonth} {stk.commodity.measurementUnit}</td>
                      <td style={{ fontWeight: 800, color: 'var(--teal-accent)' }}>
                        {stk.currentQuantity} {stk.commodity.measurementUnit}
                      </td>
                      <td>
                        {stk.currentQuantity < stk.commodity.bufferStockThreshold ? (
                          <span className="badge badge-danger">Low Stock</span>
                        ) : (
                          <span className="badge badge-success">Sufficient</span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
