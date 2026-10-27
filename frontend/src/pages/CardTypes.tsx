import React from 'react';
import { Layers, CheckCircle2 } from 'lucide-react';

export const CardTypes: React.FC = () => {
  const cardCategories = [
    {
      code: 'AAY',
      name: 'Antyodaya Anna Yojana',
      color: 'YELLOW',
      colorHex: '#F59E0B',
      desc: 'Poorest of the poor families identified under NFSA criteria. Highest subsidized grain allocation.',
      quota: 'Fixed 35 KG Rice / Month Free + 1 KG Sugar @ ₹13.50 + 1 Pkt Oil @ ₹25 + 1 KG Dal @ ₹30',
      beneficiaries: '~20,000 households in Taluk'
    },
    {
      code: 'PHH',
      name: 'Priority Household',
      color: 'PINK',
      colorHex: '#EC4899',
      desc: 'Vulnerable low-income households ensuring minimum per capita nutrition per member.',
      quota: '5 KG Rice per member (up to 20 KG cap) + 5 KG Wheat + 1 KG Sugar + 1 Pkt Oil + 1 KG Dal',
      beneficiaries: '~65,000 households in Taluk'
    },
    {
      code: 'NPHH',
      name: 'Non-Priority Household',
      color: 'WHITE',
      colorHex: '#3B82F6',
      desc: 'General state welfare households entitled to standard subsidized commodities.',
      quota: '10 KG Base + 3 KG per member up to 20 KG Rice + 5 KG Wheat + 0.5 KG Sugar',
      beneficiaries: '~12,000 households in Taluk'
    },
    {
      code: 'AY',
      name: 'Annapurna Yojana',
      color: 'GREEN',
      colorHex: '#10B981',
      desc: 'Destitute senior citizens (65+ years) without family pension or livelihood support.',
      quota: 'Fixed 10 KG Free Rice per Month',
      beneficiaries: '~3,000 households in Taluk'
    }
  ];

  return (
    <div className="page-wrapper">
      <div style={{ marginBottom: '1.5rem' }}>
        <h2>Ration Card Categories &amp; Policy Matrix</h2>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          National Food Security Act (NFSA) and Tamil Nadu state welfare card classifications
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.5rem' }}>
        {cardCategories.map((c) => (
          <div key={c.code} className="card" style={{ borderTop: `4px solid ${c.colorHex}` }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
              <span className="badge" style={{ background: `${c.colorHex}20`, color: c.colorHex, fontSize: '0.85rem' }}>
                {c.code}
              </span>
              <span style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)' }}>
                {c.beneficiaries}
              </span>
            </div>
            <h3 style={{ fontSize: '1.2rem', marginBottom: '0.5rem' }}>{c.name}</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginBottom: '1.25rem', minHeight: '40px' }}>
              {c.desc}
            </p>

            <div style={{ background: '#F8FAFC', padding: '1rem', borderRadius: '8px', border: '1px solid #E2E8F0' }}>
              <div style={{ fontSize: '0.75rem', fontWeight: 700, color: '#64748B', textTransform: 'uppercase', marginBottom: '0.35rem' }}>
                Standard Monthly Entitlement Rule:
              </div>
              <div style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--primary-navy)' }}>
                {c.quota}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
