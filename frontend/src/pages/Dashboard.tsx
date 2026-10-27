import React, { useEffect, useState } from 'react';
import { 
  CreditCard, Store, AlertTriangle, CalendarCheck, ShoppingCart, 
  MessageSquare, Warehouse, Scale, TrendingUp, Sparkles, ArrowRight
} from 'lucide-react';
import { 
  ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, 
  CartesianGrid, Legend, PieChart, Pie, Cell 
} from 'recharts';
import { api } from '../services/api';
import { DashboardStats } from '../types';

interface DashboardProps {
  onNavigate: (tab: string) => void;
}

export const Dashboard: React.FC<DashboardProps> = ({ onNavigate }) => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      setLoading(true);
      const data = await api.getDashboardStats();
      setStats(data);
    } catch (_) {
      // Fallback demo statistics
      setStats({
        totalActiveCards: 100000,
        totalShops: 120,
        lowStockShops: 3,
        pendingAllotments: 4,
        completedSalesCount: 18450,
        totalSalesRevenue: 124650.00,
        openComplaints: 2,
        unresolvedReconciliations: 2,
        godownRiceStockMt: 125.00,
        godownWheatStockMt: 45.00
      });
    } finally {
      setLoading(false);
    }
  };

  const distributionData = [
    { commodity: 'Rice (Common)', allotted: 1200, distributed: 1010 },
    { commodity: 'Whole Wheat', allotted: 300, distributed: 255 },
    { commodity: 'Cane Sugar', allotted: 120, distributed: 102 },
    { commodity: 'Edible Oil (Pkts)', allotted: 100, distributed: 86 },
    { commodity: 'Toor Dal', allotted: 100, distributed: 82 }
  ];

  const cardCategoryData = [
    { name: 'Priority (PHH)', value: 65, color: '#007A78' },
    { name: 'Antyodaya (AAY)', value: 20, color: '#F59E0B' },
    { name: 'Non-Priority (NPHH)', value: 12, color: '#3B82F6' },
    { name: 'Annapurna (AY)', value: 3, color: '#10B981' }
  ];

  return (
    <div className="page-wrapper">
      {/* Title & Actions */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Taluk Civil Supplies Control Board</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
            Tambaram Taluk • 120 Fair-Price Shops Serving 1,00,000 Beneficiary Cards
          </p>
        </div>
        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <button onClick={() => onNavigate('sales-terminal')} className="btn btn-teal">
            <ShoppingCart size={18} />
            <span>Open POS Terminal</span>
          </button>
          <button onClick={() => onNavigate('ai-assistant')} className="btn btn-primary">
            <Sparkles size={18} />
            <span>AI Officer Assistant</span>
          </button>
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div className="kpi-grid">
        <div className="kpi-card success">
          <div>
            <div className="kpi-title">Active Ration Cards</div>
            <div className="kpi-value">{stats ? stats.totalActiveCards.toLocaleString() : '1,00,000'}</div>
            <div style={{ fontSize: '0.75rem', color: '#10B981', fontWeight: 600, marginTop: '0.2rem' }}>
              4 Welfare Categories
            </div>
          </div>
          <div className="kpi-icon"><CreditCard size={24} /></div>
        </div>

        <div className="kpi-card">
          <div>
            <div className="kpi-title">Registered FPS Network</div>
            <div className="kpi-value">{stats ? stats.totalShops : 120}</div>
            <div style={{ fontSize: '0.75rem', color: '#007A78', fontWeight: 600, marginTop: '0.2rem' }}>
              100% Biometric Connected
            </div>
          </div>
          <div className="kpi-icon"><Store size={24} /></div>
        </div>

        <div className="kpi-card danger">
          <div>
            <div className="kpi-title">Low Stock Warnings</div>
            <div className="kpi-value">{stats ? stats.lowStockShops : 3}</div>
            <div style={{ fontSize: '0.75rem', color: '#EF4444', fontWeight: 600, marginTop: '0.2rem' }}>
              Immediate Indent Needed
            </div>
          </div>
          <div className="kpi-icon" style={{ background: '#FEF2F2', color: '#EF4444' }}>
            <AlertTriangle size={24} />
          </div>
        </div>

        <div className="kpi-card warning">
          <div>
            <div className="kpi-title">Pending Allotments</div>
            <div className="kpi-value">{stats ? stats.pendingAllotments : 4}</div>
            <div style={{ fontSize: '0.75rem', color: '#F59E0B', fontWeight: 600, marginTop: '0.2rem' }}>
              Awaiting Officer Approval
            </div>
          </div>
          <div className="kpi-icon" style={{ background: '#FFFBEB', color: '#F59E0B' }}>
            <CalendarCheck size={24} />
          </div>
        </div>
      </div>

      {/* Secondary KPI row */}
      <div className="kpi-grid" style={{ marginBottom: '2rem' }}>
        <div className="kpi-card">
          <div>
            <div className="kpi-title">Recorded Sales (MTD)</div>
            <div className="kpi-value">{stats ? stats.completedSalesCount.toLocaleString() : '18,450'}</div>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
              Revenue: ₹{stats ? stats.totalSalesRevenue.toLocaleString() : '1,24,650'}
            </div>
          </div>
          <div className="kpi-icon"><ShoppingCart size={24} /></div>
        </div>

        <div className="kpi-card">
          <div>
            <div className="kpi-title">Central Godown Grain Reserve</div>
            <div className="kpi-value">{stats ? stats.godownRiceStockMt : 125} MT</div>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
              Rice: {stats ? stats.godownRiceStockMt : 125} MT • Wheat: {stats ? stats.godownWheatStockMt : 45} MT
            </div>
          </div>
          <div className="kpi-icon"><Warehouse size={24} /></div>
        </div>

        <div className="kpi-card warning">
          <div>
            <div className="kpi-title">Open Citizen Grievances</div>
            <div className="kpi-value">{stats ? stats.openComplaints : 2}</div>
            <div style={{ fontSize: '0.75rem', color: '#F59E0B', fontWeight: 600, marginTop: '0.2rem' }}>
              Under Officer Triage
            </div>
          </div>
          <div className="kpi-icon" style={{ background: '#FFFBEB', color: '#F59E0B' }}><MessageSquare size={24} /></div>
        </div>

        <div className="kpi-card danger">
          <div>
            <div className="kpi-title">Unreconciled Variances</div>
            <div className="kpi-value">{stats ? stats.unresolvedReconciliations : 2}</div>
            <div style={{ fontSize: '0.75rem', color: '#EF4444', fontWeight: 600, marginTop: '0.2rem' }}>
              AI Anomaly Flagged
            </div>
          </div>
          <div className="kpi-icon" style={{ background: '#FEF2F2', color: '#EF4444' }}><Scale size={24} /></div>
        </div>
      </div>

      {/* Visual Analytics Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '1.5rem', marginBottom: '2rem' }}>
        {/* Commodity Distribution Chart */}
        <div className="card">
          <div className="card-header">
            <h3>
              <TrendingUp size={20} color="var(--teal-accent)" />
              Monthly Quota Allocated vs. Distributed (Metric Tons)
            </h3>
            <span className="badge badge-info">October 2026</span>
          </div>
          <div style={{ width: '100%', height: 320 }}>
            <ResponsiveContainer>
              <BarChart data={distributionData} margin={{ top: 10, right: 20, left: 0, bottom: 20 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E2E8F0" />
                <XAxis dataKey="commodity" stroke="#64748B" fontSize={12} />
                <YAxis stroke="#64748B" fontSize={12} />
                <Tooltip 
                  contentStyle={{ background: '#0B2545', color: '#FFFFFF', borderRadius: '8px', border: 'none' }}
                  itemStyle={{ color: '#FFFFFF' }}
                />
                <Legend />
                <Bar dataKey="allotted" name="Allotted Quota" fill="#0B2545" radius={[4, 4, 0, 0]} />
                <Bar dataKey="distributed" name="Distributed (e-POS)" fill="#00A896" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Card Categories Pie */}
        <div className="card">
          <div className="card-header">
            <h3>Cardholder Distribution</h3>
            <span className="badge badge-success">Active 1 Lakh</span>
          </div>
          <div style={{ width: '100%', height: 240 }}>
            <ResponsiveContainer>
              <PieChart>
                <Pie 
                  data={cardCategoryData} 
                  innerRadius={60} 
                  outerRadius={85} 
                  paddingAngle={4} 
                  dataKey="value"
                >
                  {cardCategoryData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem', marginTop: '0.5rem' }}>
            {cardCategoryData.map((cat, i) => (
              <div key={i} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem' }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <span style={{ width: 10, height: 10, borderRadius: '50%', background: cat.color }}></span>
                  {cat.name}
                </span>
                <span style={{ fontWeight: 700 }}>{cat.value}%</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Operational Quick Actions Banner */}
      <div className="card" style={{ background: 'linear-gradient(135deg, #0B2545 0%, #134074 100%)', color: '#FFFFFF' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h3 style={{ color: '#FFFFFF', fontSize: '1.25rem', marginBottom: '0.25rem' }}>
              Supply Chain Anomaly Radar Detected 2 High-Risk Variations
            </h3>
            <p style={{ color: '#E2E8F0', fontSize: '0.85rem' }}>
              Inspection orders ready for joint weighment audit at Chromepet Main (#3) and Chitlapakkam (#7).
            </p>
          </div>
          <button 
            onClick={() => onNavigate('ai-anomalies')} 
            className="btn btn-teal"
            style={{ whiteSpace: 'nowrap' }}
          >
            <span>Review Anomaly Radar</span>
            <ArrowRight size={16} />
          </button>
        </div>
      </div>
    </div>
  );
};
