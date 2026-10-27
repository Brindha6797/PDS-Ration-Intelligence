import React, { useEffect, useState } from 'react';
import { Package, ShieldCheck } from 'lucide-react';
import { api } from '../services/api';
import { Commodity } from '../types';

export const Commodities: React.FC = () => {
  const [commodities, setCommodities] = useState<Commodity[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadCommodities();
  }, []);

  const loadCommodities = async () => {
    try {
      setLoading(true);
      const data = await api.getCommodities();
      setCommodities(data);
    } catch (_) {
      // Fallback demo commodities
      setCommodities([
        { commodityId: 1, commodityCode: 'RICE_COMMON', commodityName: 'Common Rice (PDS)', measurementUnit: 'KG', consumerRatePerUnit: 0.0, governmentSubsidyPerUnit: 34.50, bufferStockThreshold: 500.0, isActive: 'Y' },
        { commodityId: 2, commodityCode: 'WHEAT', commodityName: 'Whole Wheat Grain', measurementUnit: 'KG', consumerRatePerUnit: 2.0, governmentSubsidyPerUnit: 26.80, bufferStockThreshold: 300.0, isActive: 'Y' },
        { commodityId: 3, commodityCode: 'SUGAR', commodityName: 'Refined Cane Sugar', measurementUnit: 'KG', consumerRatePerUnit: 13.50, governmentSubsidyPerUnit: 24.00, bufferStockThreshold: 150.0, isActive: 'Y' },
        { commodityId: 4, commodityCode: 'PALM_OIL', commodityName: 'Fortified Edible Palm Oil', measurementUnit: 'PACKET', consumerRatePerUnit: 25.0, governmentSubsidyPerUnit: 65.00, bufferStockThreshold: 100.0, isActive: 'Y' },
        { commodityId: 5, commodityCode: 'TOOR_DAL', commodityName: 'Split Pigeon Pea (Toor Dal)', measurementUnit: 'KG', consumerRatePerUnit: 30.0, governmentSubsidyPerUnit: 85.00, bufferStockThreshold: 150.0, isActive: 'Y' },
        { commodityId: 6, commodityCode: 'KEROSENE', commodityName: 'Subsidized Blue Kerosene', measurementUnit: 'LITRE', consumerRatePerUnit: 15.0, governmentSubsidyPerUnit: 38.00, bufferStockThreshold: 80.0, isActive: 'Y' }
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-wrapper">
      <div style={{ marginBottom: '1.5rem' }}>
        <h2>Government Subsidized Commodity Register</h2>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          Standard consumer welfare rates and state civil supplies subsidy subventions
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.5rem' }}>
        {commodities.map((comm) => (
          <div key={comm.commodityId} className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
              <span className="badge badge-info">{comm.commodityCode}</span>
              <span className="badge badge-success">Active Subsidized</span>
            </div>
            <h3 style={{ fontSize: '1.25rem', marginBottom: '0.25rem' }}>{comm.commodityName}</h3>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '1.25rem' }}>
              Measurement Unit: <strong>{comm.measurementUnit}</strong>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem', background: '#F8FAFC', padding: '1rem', borderRadius: '8px', border: '1px solid #E2E8F0' }}>
              <div>
                <div style={{ fontSize: '0.7rem', color: '#64748B', fontWeight: 600, textTransform: 'uppercase' }}>Consumer Rate</div>
                <div style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--primary-navy)' }}>
                  {comm.consumerRatePerUnit === 0 ? 'FREE' : `₹${comm.consumerRatePerUnit.toFixed(2)}`}
                </div>
              </div>
              <div>
                <div style={{ fontSize: '0.7rem', color: '#64748B', fontWeight: 600, textTransform: 'uppercase' }}>State Subsidy</div>
                <div style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--teal-accent)' }}>
                  ₹{comm.governmentSubsidyPerUnit.toFixed(2)}
                </div>
              </div>
            </div>

            <div style={{ marginTop: '0.75rem', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
              Shop Safety Buffer Threshold: <strong>{comm.bufferStockThreshold} {comm.measurementUnit}</strong>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
