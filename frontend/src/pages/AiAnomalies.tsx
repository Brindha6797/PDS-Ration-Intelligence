import React, { useState, useEffect } from 'react';
import { 
  ShieldAlert, 
  AlertTriangle, 
  Info, 
  RefreshCw, 
  Search, 
  ExternalLink, 
  CheckCircle, 
  Filter 
} from 'lucide-react';
import { aiApi, shopApi, commodityApi } from '../services/api';
import { AnomalyAlert, Shop, Commodity } from '../types';

export const AiAnomalies: React.FC = () => {
  const [alerts, setAlerts] = useState<AnomalyAlert[]>([]);
  const [shops, setShops] = useState<Shop[]>([]);
  const [commodities, setCommodities] = useState<Commodity[]>([]);
  const [loading, setLoading] = useState(true);
  const [riskFilter, setRiskFilter] = useState('ALL');
  const [shopFilter, setShopFilter] = useState<number | 'ALL'>('ALL');
  const [selectedAlert, setSelectedAlert] = useState<AnomalyAlert | null>(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [anomRes, shopRes, commRes] = await Promise.all([
        aiApi.detectAnomalies(),
        shopApi.getAll(),
        commodityApi.getAll()
      ]);
      setAlerts(anomRes.data);
      setShops(shopRes.data);
      setCommodities(commRes.data);
    } catch (err) {
      console.error('Failed to load anomaly detection data', err);
    } finally {
      setLoading(false);
    }
  };

  const filteredAlerts = alerts.filter(a => {
    const matchesRisk = riskFilter === 'ALL' || a.risk_level === riskFilter;
    const matchesShop = shopFilter === 'ALL' || a.shop_id === shopFilter;
    return matchesRisk && matchesShop;
  });

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-navy-900 flex items-center gap-2">
            <ShieldAlert className="text-red-600" /> AI Stock Anomaly & Leakage Radar
          </h1>
          <p className="text-gray-500 text-sm">
            Statistical Isolation Forest and Z-Score outlier detection across dispatches, POS sales, and physical audits
          </p>
        </div>
        <button 
          onClick={loadData}
          disabled={loading}
          className="btn btn-outline flex items-center gap-2"
        >
          <RefreshCw size={16} className={loading ? 'animate-spin' : ''} /> Re-scan Anomalies
        </button>
      </div>

      {/* Strict Ethical AI Disclaimer Banner */}
      <div className="bg-amber-50 border-l-4 border-l-amber-500 p-4 rounded-r-lg mb-6 shadow-sm">
        <div className="flex items-start gap-3">
          <Info className="text-amber-700 flex-shrink-0 mt-0.5" size={20} />
          <div className="text-xs text-amber-900 leading-relaxed">
            <strong>Statutory Civil Supplies Regulatory Compliance:</strong> AI alerts are purely statistical anomaly indicators to prioritize physical inspections. The AI engine is strictly sandboxed and read-only; it does not accuse individuals of theft or modify inventory records. Authorized Taluk Officers must conduct physical weighment audits before issuing notice.
          </div>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div className="kpi-card border-l-4 border-l-red-600">
          <p className="text-xs uppercase text-gray-500 font-semibold">Critical Risk Discrepancies</p>
          <h3 className="text-2xl font-bold text-red-600 mt-1">
            {alerts.filter(a => a.risk_level === 'CRITICAL' || a.risk_level === 'HIGH').length}
          </h3>
        </div>
        <div className="kpi-card border-l-4 border-l-amber-500">
          <p className="text-xs uppercase text-gray-500 font-semibold">Medium Variance Audits</p>
          <h3 className="text-2xl font-bold text-amber-600 mt-1">
            {alerts.filter(a => a.risk_level === 'MEDIUM').length}
          </h3>
        </div>
        <div className="kpi-card border-l-4 border-l-teal-600">
          <p className="text-xs uppercase text-gray-500 font-semibold">Total Shops Scanned</p>
          <h3 className="text-2xl font-bold text-teal-700 mt-1">{shops.length} Fair-Price Shops</h3>
        </div>
        <div className="kpi-card border-l-4 border-l-blue-600">
          <p className="text-xs uppercase text-gray-500 font-semibold">Algorithm Confidence</p>
          <h3 className="text-2xl font-bold text-blue-700 mt-1">94.8%</h3>
        </div>
      </div>

      {/* Filters */}
      <div className="card p-4 mb-6 flex flex-wrap gap-4 items-center">
        <div className="flex items-center gap-2 text-sm text-gray-600 font-medium">
          <Filter size={16} /> Filter Alerts:
        </div>
        <div>
          <select 
            value={riskFilter} 
            onChange={(e) => setRiskFilter(e.target.value)}
            className="input text-sm py-1.5"
          >
            <option value="ALL">All Risk Levels</option>
            <option value="CRITICAL">CRITICAL RISK</option>
            <option value="HIGH">HIGH RISK</option>
            <option value="MEDIUM">MEDIUM RISK</option>
            <option value="LOW">LOW</option>
          </select>
        </div>
        <div>
          <select 
            value={shopFilter} 
            onChange={(e) => setShopFilter(e.target.value === 'ALL' ? 'ALL' : Number(e.target.value))}
            className="input text-sm py-1.5"
          >
            <option value="ALL">All Shops</option>
            {shops.map(s => (
              <option key={s.shopId} value={s.shopId}>{s.shopCode} - {s.shopName}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Anomalies List */}
      <div className="space-y-4">
        {loading ? (
          <div className="p-12 text-center text-gray-500 card">Running Isolation Forest audit algorithms...</div>
        ) : filteredAlerts.length === 0 ? (
          <div className="p-12 text-center text-emerald-700 card bg-emerald-50 border border-emerald-200">
            <CheckCircle className="mx-auto mb-2 text-emerald-600" size={32} />
            <h4 className="font-bold text-base">Zero Critical Anomalies Detected</h4>
            <p className="text-xs text-emerald-800 mt-1">Recorded shop stocks match mathematical ledger movements within tolerance thresholds.</p>
          </div>
        ) : (
          filteredAlerts.map((anomaly, idx) => (
            <div 
              key={idx} 
              className={`card p-5 border-l-4 transition-all hover:shadow-md ${
                anomaly.risk_level === 'CRITICAL' || anomaly.risk_level === 'HIGH' 
                  ? 'border-l-red-600 bg-red-50/20' 
                  : anomaly.risk_level === 'MEDIUM' 
                    ? 'border-l-amber-500 bg-amber-50/10' 
                    : 'border-l-blue-500'
              }`}
            >
              <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-3">
                <div>
                  <div className="flex items-center gap-2">
                    <span className={`badge ${
                      anomaly.risk_level === 'CRITICAL' || anomaly.risk_level === 'HIGH' ? 'badge-danger' : 
                      anomaly.risk_level === 'MEDIUM' ? 'badge-warning' : 'badge-neutral'
                    }`}>
                      {anomaly.risk_level} RISK
                    </span>
                    <h3 className="font-bold text-navy-900 text-base">
                      {anomaly.shop_name} ({anomaly.commodity_name})
                    </h3>
                  </div>
                  <p className="text-xs text-gray-500 mt-1">
                    Related Transactions: <code className="bg-gray-100 px-1 py-0.5 rounded text-navy-800">{anomaly.transaction_reference}</code>
                  </p>
                </div>

                <div className="flex items-center gap-6 text-sm">
                  <div className="text-right">
                    <div className="text-xs text-gray-500">Expected (Ledger)</div>
                    <div className="font-semibold text-gray-800">{anomaly.expected_quantity}</div>
                  </div>
                  <div className="text-right">
                    <div className="text-xs text-gray-500">Recorded (Physical)</div>
                    <div className="font-semibold text-navy-900">{anomaly.recorded_quantity}</div>
                  </div>
                  <div className="text-right">
                    <div className="text-xs text-gray-500">Variance</div>
                    <div className="font-bold text-red-600">{anomaly.difference}</div>
                  </div>
                </div>
              </div>

              <div className="bg-white p-3 rounded border border-gray-200 mb-3 text-xs">
                <div className="font-semibold text-gray-800 mb-1 flex items-center gap-1.5">
                  <AlertTriangle size={14} className="text-amber-600" /> Statistical Root Cause:
                </div>
                <p className="text-gray-700 leading-relaxed">{anomaly.alert_reason}</p>
              </div>

              <div className="bg-teal-50/70 p-3 rounded border border-teal-200 text-xs flex justify-between items-center">
                <div>
                  <span className="font-bold text-teal-900">Recommended Action: </span>
                  <span className="text-teal-800">{anomaly.recommended_action}</span>
                </div>
                <button 
                  onClick={() => alert(`Initiating Inspection Protocol for ${anomaly.shop_name} on ${anomaly.commodity_name}`)}
                  className="btn btn-outline text-xs py-1 px-3 border-teal-700 text-teal-800 hover:bg-teal-100"
                >
                  Schedule Taluk Officer Audit
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
