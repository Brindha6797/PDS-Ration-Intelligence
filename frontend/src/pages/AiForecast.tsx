import React, { useState, useEffect } from 'react';
import { 
  TrendingUp, 
  AlertOctagon, 
  CheckCircle, 
  Calendar, 
  Search, 
  Download, 
  Truck, 
  Info 
} from 'lucide-react';
import { aiApi, shopApi, commodityApi } from '../services/api';
import { DemandForecast, Shop, Commodity } from '../types';

export const AiForecast: React.FC = () => {
  const [forecasts, setForecasts] = useState<DemandForecast[]>([]);
  const [shops, setShops] = useState<Shop[]>([]);
  const [commodities, setCommodities] = useState<Commodity[]>([]);
  const [loading, setLoading] = useState(true);
  const [urgencyFilter, setUrgencyFilter] = useState('ALL');
  const [shopFilter, setShopFilter] = useState<number | 'ALL'>('ALL');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [foreRes, shopRes, commRes] = await Promise.all([
        aiApi.getDemandForecast(),
        shopApi.getAll(),
        commodityApi.getAll()
      ]);
      setForecasts(foreRes.data);
      setShops(shopRes.data);
      setCommodities(commRes.data);
    } catch (err) {
      console.error('Failed to load demand forecasts', err);
    } finally {
      setLoading(false);
    }
  };

  const filtered = forecasts.filter(f => {
    const matchesUrgency = urgencyFilter === 'ALL' || f.urgency === urgencyFilter;
    const matchesShop = shopFilter === 'ALL' || f.shop_id === shopFilter;
    return matchesUrgency && matchesShop;
  });

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-navy-900 flex items-center gap-2">
            <TrendingUp className="text-teal-600" /> AI Demand Forecasting & Replenishment
          </h1>
          <p className="text-gray-500 text-sm">
            Predictive consumption run-rate models, stockout horizons, and automated depot indent recommendations
          </p>
        </div>
        <button 
          onClick={loadData}
          className="btn btn-primary flex items-center gap-2"
        >
          <Calendar size={16} /> Refresh 30-Day Projections
        </button>
      </div>

      <div className="bg-blue-50 border-l-4 border-l-blue-600 p-4 rounded-r mb-6 text-xs text-blue-900 leading-relaxed">
        <strong>Forecasting Methodology:</strong> Estimates combine cardholder baseline entitlements, historical seasonal lift factors (e.g., festival quotas), and 90-day moving sales velocity. Replenishment dispatches remain subject to Civil Supplies Tahsildar approval.
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div className="kpi-card border-l-4 border-l-red-600">
          <p className="text-xs uppercase text-gray-500 font-semibold">Immediate Stockout Risk (&lt; 7 Days)</p>
          <h3 className="text-2xl font-bold text-red-600 mt-1">
            {forecasts.filter(f => f.urgency === 'CRITICAL' || f.days_of_stock_remaining < 7).length} Outlets
          </h3>
        </div>
        <div className="kpi-card border-l-4 border-l-amber-500">
          <p className="text-xs uppercase text-gray-500 font-semibold">Moderate Buffer (7 - 14 Days)</p>
          <h3 className="text-2xl font-bold text-amber-600 mt-1">
            {forecasts.filter(f => f.urgency === 'HIGH' || f.urgency === 'MODERATE').length} Outlets
          </h3>
        </div>
        <div className="kpi-card border-l-4 border-l-emerald-600">
          <p className="text-xs uppercase text-gray-500 font-semibold">Comfortable Inventory (&gt; 14 Days)</p>
          <h3 className="text-2xl font-bold text-emerald-600 mt-1">
            {forecasts.filter(f => f.urgency === 'SAFE').length} Outlets
          </h3>
        </div>
        <div className="kpi-card border-l-4 border-l-teal-600">
          <p className="text-xs uppercase text-gray-500 font-semibold">Forecast Window</p>
          <h3 className="text-2xl font-bold text-teal-700 mt-1">Next 30 Days</h3>
        </div>
      </div>

      {/* Filters */}
      <div className="card p-4 mb-6 flex flex-wrap gap-4 items-center">
        <div>
          <label className="text-xs font-semibold text-gray-500 block mb-1">Filter by Horizon:</label>
          <select 
            value={urgencyFilter} 
            onChange={(e) => setUrgencyFilter(e.target.value)}
            className="input text-sm py-1.5"
          >
            <option value="ALL">All Risk Levels</option>
            <option value="CRITICAL">CRITICAL (&lt; 5 Days)</option>
            <option value="HIGH">HIGH (&lt; 10 Days)</option>
            <option value="MODERATE">MODERATE (10-15 Days)</option>
            <option value="SAFE">SAFE (&gt; 15 Days)</option>
          </select>
        </div>
        <div>
          <label className="text-xs font-semibold text-gray-500 block mb-1">Filter by Fair-Price Shop:</label>
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

      {/* Forecast Table */}
      <div className="card overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-gray-500">Calculating predictive inventory run-rates...</div>
        ) : filtered.length === 0 ? (
          <div className="p-8 text-center text-gray-500">No forecasts matching selected filters.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="table">
              <thead>
                <tr>
                  <th>Fair-Price Shop</th>
                  <th>Commodity</th>
                  <th>Current Stock</th>
                  <th>Estimated 30-Day Demand</th>
                  <th>Days of Stock Left</th>
                  <th>Status & Alert</th>
                  <th>Recommended Replenishment</th>
                  <th>Officer Action</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((item, idx) => (
                  <tr key={idx}>
                    <td>
                      <div className="font-semibold text-navy-900">{item.shop_name}</div>
                      <div className="text-xs text-gray-500">Period: {item.forecast_period}</div>
                    </td>
                    <td className="font-medium text-gray-800">{item.commodity_name}</td>
                    <td className="font-semibold text-gray-900">{item.available_stock}</td>
                    <td className="font-semibold text-teal-700">{item.estimated_demand}</td>
                    <td>
                      <span className={`font-bold ${
                        item.days_of_stock_remaining < 7 ? 'text-red-600' :
                        item.days_of_stock_remaining < 14 ? 'text-amber-600' : 'text-emerald-600'
                      }`}>
                        {item.days_of_stock_remaining} Days
                      </span>
                    </td>
                    <td>
                      <span className={`badge ${
                        item.urgency === 'CRITICAL' ? 'badge-danger' :
                        item.urgency === 'HIGH' ? 'badge-warning' :
                        item.urgency === 'MODERATE' ? 'badge-primary' : 'badge-success'
                      }`}>
                        {item.potential_shortage_alert}
                      </span>
                    </td>
                    <td>
                      <div className="font-bold text-navy-900">
                        {item.recommended_replenishment}
                      </div>
                    </td>
                    <td>
                      <button 
                        onClick={() => alert(`Replenishment indent drafted for ${item.shop_name}: ${item.recommended_replenishment}`)}
                        className="btn btn-outline text-xs py-1 px-2 flex items-center gap-1 text-teal-700 border-teal-600"
                      >
                        <Truck size={14} /> Draft Indent
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
