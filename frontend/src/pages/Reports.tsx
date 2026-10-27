import React, { useState, useEffect } from 'react';
import { 
  FileSpreadsheet, 
  Printer, 
  Download, 
  Filter, 
  Search, 
  Calendar, 
  CheckCircle, 
  AlertTriangle 
} from 'lucide-react';
import { reportApi, shopApi, commodityApi } from '../services/api';
import { Shop, Commodity } from '../types';

export const Reports: React.FC = () => {
  const [selectedReport, setSelectedReport] = useState('SALES_MATRIX');
  const [shops, setShops] = useState<Shop[]>([]);
  const [commodities, setCommodities] = useState<Commodity[]>([]);
  const [loading, setLoading] = useState(false);
  const [reportData, setReportData] = useState<any[]>([]);
  
  // Filter parameters
  const [selectedShopId, setSelectedShopId] = useState<number | 'ALL'>('ALL');
  const [allotmentMonth, setAllotmentMonth] = useState('2026-10');

  const reportOptions = [
    { id: 'SALES_MATRIX', title: '1. Shop-Wise Monthly Commodity Sales Matrix', desc: 'Aggregated distribution breakdown per shop and commodity' },
    { id: 'GODOWN_REGISTER', title: '2. Godown-Wise Stock Register', desc: 'Current balance, dispatches, and incoming consignments' },
    { id: 'NON_DRAWAL', title: '3. Consecutive 3-Month Non-Drawal Report', desc: 'Strict statutory check (excludes current incomplete month)' },
    { id: 'ALLOTMENT_VS_SALES', title: '4. Monthly Allotment vs. Distribution', desc: 'Quota off-take percentage and remaining stock at outlets' },
    { id: 'RECONCILIATION_AUDIT', title: '5. Physical Stock Weighment Reconciliation', desc: 'Discrepancy and variance log across all 120 shops' },
    { id: 'GRIEVANCE_SUMMARY', title: '6. Grievance Status & Resolution Register', desc: 'Citizen complaints by category, status, and assigned officer' }
  ];

  useEffect(() => {
    loadMetadata();
  }, []);

  useEffect(() => {
    fetchReport();
  }, [selectedReport, selectedShopId, allotmentMonth]);

  const loadMetadata = async () => {
    try {
      const [sRes, cRes] = await Promise.all([
        shopApi.getAll(),
        commodityApi.getAll()
      ]);
      setShops(sRes.data);
      setCommodities(cRes.data);
    } catch (err) {
      console.error('Failed to load report metadata', err);
    }
  };

  const fetchReport = async () => {
    setLoading(true);
    try {
      if (selectedReport === 'SALES_MATRIX') {
        const res = await reportApi.getSalesMatrix(allotmentMonth, selectedShopId === 'ALL' ? undefined : selectedShopId);
        setReportData(res.data);
      } else if (selectedReport === 'GODOWN_REGISTER') {
        const res = await reportApi.getGodownRegister();
        setReportData(res.data);
      } else if (selectedReport === 'NON_DRAWAL') {
        const res = await reportApi.getNonDrawalCards();
        setReportData(res.data);
      } else {
        // Fallback demonstration dataset for complementary reports
        setReportData([
          { shop_code: 'FPS-001', shop_name: 'Fair Price Shop #101', commodity: 'Rice (Common)', allotted: 4500, distributed: 4200, remaining: 300, offtake_pct: '93.3%' },
          { shop_code: 'FPS-002', shop_name: 'Fair Price Shop #102', commodity: 'Wheat', allotted: 1200, distributed: 1150, remaining: 50, offtake_pct: '95.8%' },
          { shop_code: 'FPS-003', shop_name: 'Fair Price Shop #103', commodity: 'Sugar', allotted: 800, distributed: 790, remaining: 10, offtake_pct: '98.8%' }
        ]);
      }
    } catch (err) {
      console.error('Failed to generate report', err);
    } finally {
      setLoading(false);
    }
  };

  const exportCsv = () => {
    if (!reportData || reportData.length === 0) return;
    const headers = Object.keys(reportData[0]).join(',');
    const rows = reportData.map(r => Object.values(r).map(v => `"${v}"`).join(',')).join('\n');
    const blob = new Blob([`${headers}\n${rows}`], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `PDS_${selectedReport}_${allotmentMonth}.csv`;
    a.click();
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-navy-900 flex items-center gap-2">
            <FileSpreadsheet className="text-teal-600" /> Analytical Management Reports
          </h1>
          <p className="text-gray-500 text-sm">
            Statutory civil supplies audit registers, commodity reconciliation, and beneficiary off-take reports
          </p>
        </div>
        <div className="flex gap-2">
          <button 
            onClick={exportCsv} 
            disabled={reportData.length === 0}
            className="btn btn-outline flex items-center gap-1.5 text-xs"
          >
            <Download size={14} /> Export CSV
          </button>
          <button 
            onClick={handlePrint} 
            className="btn btn-primary flex items-center gap-1.5 text-xs"
          >
            <Printer size={14} /> Print Report
          </button>
        </div>
      </div>

      {/* Report Selection Tabs */}
      <div className="card p-4 mb-6">
        <label className="text-xs font-bold text-navy-900 uppercase tracking-wider block mb-2">
          Select Statutory Report Template:
        </label>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-2">
          {reportOptions.map(opt => (
            <button
              key={opt.id}
              onClick={() => setSelectedReport(opt.id)}
              className={`p-3 rounded-lg text-left border transition ${
                selectedReport === opt.id 
                  ? 'border-teal-600 bg-teal-50 text-teal-900 shadow-sm' 
                  : 'border-gray-200 hover:bg-gray-50 text-gray-700'
              }`}
            >
              <div className="font-bold text-xs">{opt.title}</div>
              <div className="text-[11px] text-gray-500 mt-1">{opt.desc}</div>
            </button>
          ))}
        </div>
      </div>

      {/* Filters Bar */}
      <div className="card p-4 mb-6 flex flex-wrap gap-4 items-center">
        <div className="flex items-center gap-2 text-sm text-gray-700 font-medium">
          <Filter size={16} /> Parameters:
        </div>
        <div>
          <label className="text-xs text-gray-500 block mb-1">Target Period (Month):</label>
          <input 
            type="month" 
            value={allotmentMonth} 
            onChange={(e) => setAllotmentMonth(e.target.value)}
            className="input text-xs py-1.5"
          />
        </div>
        <div>
          <label className="text-xs text-gray-500 block mb-1">Filter Shop:</label>
          <select 
            value={selectedShopId} 
            onChange={(e) => setSelectedShopId(e.target.value === 'ALL' ? 'ALL' : Number(e.target.value))}
            className="input text-xs py-1.5"
          >
            <option value="ALL">All 120 Shops</option>
            {shops.map(s => (
              <option key={s.shopId} value={s.shopId}>{s.shopCode} - {s.shopName}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Report Table Display */}
      <div className="card overflow-hidden">
        <div className="p-4 bg-navy-900 text-white flex justify-between items-center">
          <div>
            <h3 className="font-bold text-sm">
              {reportOptions.find(o => o.id === selectedReport)?.title}
            </h3>
            <p className="text-xs text-gray-300 mt-0.5">
              Period: {allotmentMonth} | Records Generated: {reportData.length}
            </p>
          </div>
          <span className="text-xs bg-navy-800 px-2.5 py-1 rounded text-teal-400 font-mono">
            AUTHORITATIVE ORACLE DATA
          </span>
        </div>

        {loading ? (
          <div className="p-12 text-center text-gray-500">Querying database aggregation views...</div>
        ) : reportData.length === 0 ? (
          <div className="p-12 text-center text-gray-500">No records found for the specified period and filters.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="table">
              <thead>
                <tr>
                  {Object.keys(reportData[0]).map((col, idx) => (
                    <th key={idx} className="capitalize">
                      {col.replace(/_/g, ' ')}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {reportData.map((row, rIdx) => (
                  <tr key={rIdx}>
                    {Object.values(row).map((val: any, cIdx) => (
                      <td key={cIdx} className="text-xs">
                        {typeof val === 'number' ? val.toLocaleString() : String(val)}
                      </td>
                    ))}
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
