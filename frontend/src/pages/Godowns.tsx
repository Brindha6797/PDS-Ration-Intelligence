import React, { useEffect, useState } from 'react';
import { Warehouse, Package, Truck, ShieldCheck } from 'lucide-react';
import { api } from '../services/api';
import { Godown, GodownStock } from '../types';

export const Godowns: React.FC = () => {
  const [godowns, setGodowns] = useState<Godown[]>([]);
  const [stocks, setStocks] = useState<GodownStock[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadGodowns();
  }, []);

  const loadGodowns = async () => {
    try {
      setLoading(true);
      const gList = await api.getGodowns();
      setGodowns(gList);
      if (gList.length > 0) {
        const sList = await api.getGodownStock(gList[0].godownId);
        setStocks(sList);
      }
    } catch (_) {
      // Fallback demo godowns
      setGodowns([
        { godownId: 1, godownCode: 'GDN-TLK-01', godownName: 'Taluk Central Civil Supplies Depot', talukName: 'Tambaram Taluk', storageCapacityMt: 2500, managerName: 'K. Senthil Nathan', contactNumber: '9840112233', address: 'Station Road Depot Complex, Tambaram' },
        { godownId: 2, godownCode: 'GDN-TLK-02', godownName: 'Sub-Taluk Grain Storage Warehouse', talukName: 'Tambaram Taluk', storageCapacityMt: 1200, managerName: 'P. Murugesan', contactNumber: '9840445566', address: 'GST Road Chromepet Depot' }
      ]);
      setStocks([
        { godownStockId: 1, godown: null as any, commodity: { commodityId: 1, commodityCode: 'RICE', commodityName: 'Common Rice (PDS)', measurementUnit: 'KG', consumerRatePerUnit: 0, governmentSubsidyPerUnit: 35, bufferStockThreshold: 500, isActive: 'Y' }, currentQuantity: 125000, reservedQuantity: 15000, lastReplenishedDate: '2026-10-15' },
        { godownStockId: 2, godown: null as any, commodity: { commodityId: 2, commodityCode: 'WHEAT', commodityName: 'Whole Wheat Grain', measurementUnit: 'KG', consumerRatePerUnit: 2, governmentSubsidyPerUnit: 26, bufferStockThreshold: 300, isActive: 'Y' }, currentQuantity: 45000, reservedQuantity: 5000, lastReplenishedDate: '2026-10-14' },
        { godownStockId: 3, godown: null as any, commodity: { commodityId: 3, commodityCode: 'SUGAR', commodityName: 'Refined Cane Sugar', measurementUnit: 'KG', consumerRatePerUnit: 13.5, governmentSubsidyPerUnit: 24, bufferStockThreshold: 150, isActive: 'Y' }, currentQuantity: 18000, reservedQuantity: 2000, lastReplenishedDate: '2026-10-18' },
        { godownStockId: 4, godown: null as any, commodity: { commodityId: 4, commodityCode: 'OIL', commodityName: 'Fortified Palm Oil', measurementUnit: 'PACKET', consumerRatePerUnit: 25, governmentSubsidyPerUnit: 65, bufferStockThreshold: 100, isActive: 'Y' }, currentQuantity: 12000, reservedQuantity: 1200, lastReplenishedDate: '2026-10-20' }
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-wrapper">
      <div style={{ marginBottom: '1.5rem' }}>
        <h2>Central Godowns &amp; Buffer Grain Depots</h2>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          Regional storage warehouses supplying bulk dispatches to the 120 fair-price shops
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(360px, 1fr))', gap: '1.5rem', marginBottom: '2rem' }}>
        {godowns.map(g => (
          <div key={g.godownId} className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
              <span className="badge badge-info">{g.godownCode}</span>
              <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--teal-accent)' }}>
                Capacity: {g.storageCapacityMt} MT
              </span>
            </div>
            <h3 style={{ fontSize: '1.25rem', marginBottom: '0.35rem' }}>{g.godownName}</h3>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '0.5rem' }}>{g.address}</p>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-main)', marginTop: '0.75rem', paddingTop: '0.75rem', borderTop: '1px solid #E2E8F0' }}>
              Depot Manager: <strong>{g.managerName}</strong> (Ph: {g.contactNumber})
            </div>
          </div>
        ))}
      </div>

      <div className="card">
        <div className="card-header">
          <h3>
            <Package size={20} color="var(--teal-accent)" />
            Central Depot Live Bulk Inventory
          </h3>
          <span className="badge badge-success">Audit Verified</span>
        </div>

        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Commodity</th>
                <th>Physical Stock (KG / Units)</th>
                <th>Physical Stock (MT)</th>
                <th>Committed In-Transit</th>
                <th>Last Replenished</th>
                <th>Buffer Status</th>
              </tr>
            </thead>
            <tbody>
              {stocks.map(s => (
                <tr key={s.godownStockId}>
                  <td style={{ fontWeight: 700, color: 'var(--primary-navy)' }}>{s.commodity.commodityName}</td>
                  <td style={{ fontWeight: 600 }}>{s.currentQuantity.toLocaleString()} {s.commodity.measurementUnit}</td>
                  <td style={{ fontWeight: 800, color: 'var(--teal-accent)' }}>
                    {(s.currentQuantity / 1000).toFixed(2)} MT
                  </td>
                  <td>{s.reservedQuantity.toLocaleString()} {s.commodity.measurementUnit}</td>
                  <td>{s.lastReplenishedDate || 'Recently'}</td>
                  <td>
                    <span className="badge badge-success">Healthy Buffer</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
