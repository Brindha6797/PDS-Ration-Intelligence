import React, { useState } from 'react';
import { 
  History, 
  Search, 
  Filter, 
  ShieldCheck, 
  Lock, 
  FileText 
} from 'lucide-react';

interface AuditEntry {
  auditId: number;
  tableName: string;
  recordId: number;
  action: string;
  details: string;
  performedBy: string;
  ipAddress: string;
  createdAt: string;
}

export const AuditLogs: React.FC = () => {
  const [logs] = useState<AuditEntry[]>([
    {
      auditId: 1001,
      tableName: 'SALES',
      recordId: 5001,
      action: 'POS_SALE_COMPLETED',
      details: 'Card TN-04-AAY-10001 purchased 35kg Rice, 1kg Sugar. Quota atomically decremented.',
      performedBy: 'shop_001',
      ipAddress: '10.24.120.45',
      createdAt: '2026-10-27 11:45:12'
    },
    {
      auditId: 1002,
      tableName: 'DISPATCHES',
      recordId: 3001,
      action: 'DISPATCH_INWARDED',
      details: 'Shop #101 confirmed receipt of Consignment DISP-202610-001 (Rice: 4500kg, Sugar: 800kg).',
      performedBy: 'shop_001',
      ipAddress: '10.24.120.45',
      createdAt: '2026-10-27 10:15:30'
    },
    {
      auditId: 1003,
      tableName: 'ALLOTMENTS',
      recordId: 2001,
      action: 'ALLOTMENT_APPROVED',
      details: 'Tahsildar approved monthly quota for FPS #101. Total 4500kg Rice, 800kg Sugar.',
      performedBy: 'officer_pds',
      ipAddress: '10.24.100.12',
      createdAt: '2026-10-27 09:30:00'
    },
    {
      auditId: 1004,
      tableName: 'STOCK_RECONCILIATION',
      recordId: 4001,
      action: 'WEIGHMENT_AUDIT',
      details: 'Recorded physical count at FPS #101: Rice 280kg (Recorded 300kg, -20kg variance).',
      performedBy: 'shop_001',
      ipAddress: '10.24.120.45',
      createdAt: '2026-10-26 18:20:00'
    },
    {
      auditId: 1005,
      tableName: 'ENTITLEMENTS',
      recordId: 1001,
      action: 'ENTITLEMENT_GENERATED',
      details: 'Batch job generated monthly entitlement for period 2026-10 for 100,000 cards.',
      performedBy: 'SYSTEM_JOB',
      ipAddress: '127.0.0.1',
      createdAt: '2026-10-01 00:00:15'
    }
  ]);

  const [searchTerm, setSearchTerm] = useState('');
  const [tableFilter, setTableFilter] = useState('ALL');

  const filtered = logs.filter(l => {
    const matchesSearch = l.details.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          l.performedBy.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          l.action.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesTable = tableFilter === 'ALL' || l.tableName === tableFilter;
    return matchesSearch && matchesTable;
  });

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-navy-900 flex items-center gap-2">
            <History className="text-teal-600" /> Immutable System Audit Journal
          </h1>
          <p className="text-gray-500 text-sm">
            Statutory civil supplies audit trail logging all inventory updates, POS sales, and approvals
          </p>
        </div>
        <div className="flex items-center gap-2 bg-navy-900 text-white text-xs px-3 py-1.5 rounded-full">
          <Lock size={14} className="text-teal-400" />
          <span>Oracle DB Triggers &amp; Hibernate Envers Active</span>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="card p-4 mb-6 flex flex-wrap gap-4 items-center">
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3 top-2.5 text-gray-400" size={18} />
          <input 
            type="text" 
            placeholder="Search audit trail by keyword, user, action..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="input pl-10 text-xs"
          />
        </div>
        <div>
          <select 
            value={tableFilter} 
            onChange={(e) => setTableFilter(e.target.value)}
            className="input text-xs py-2"
          >
            <option value="ALL">All Relational Entities</option>
            <option value="SALES">SALES &amp; ITEMS</option>
            <option value="DISPATCHES">DISPATCHES &amp; CONSIGNMENTS</option>
            <option value="ALLOTMENTS">ALLOTMENTS</option>
            <option value="STOCK_RECONCILIATION">STOCK RECONCILIATION</option>
            <option value="ENTITLEMENTS">ENTITLEMENTS</option>
            <option value="COMPLAINTS">COMPLAINTS</option>
          </select>
        </div>
      </div>

      {/* Audit Log Table */}
      <div className="card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="table">
            <thead>
              <tr>
                <th>Log ID</th>
                <th>Timestamp</th>
                <th>Target Entity</th>
                <th>Action Type</th>
                <th>Audit Journal Description</th>
                <th>Operator</th>
                <th>IP Address</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map(entry => (
                <tr key={entry.auditId}>
                  <td className="font-mono text-xs text-gray-500">#{entry.auditId}</td>
                  <td className="text-xs text-gray-600 whitespace-nowrap">{entry.createdAt}</td>
                  <td>
                    <span className="font-mono text-xs bg-gray-100 px-2 py-0.5 rounded font-bold text-navy-800">
                      {entry.tableName}
                    </span>
                  </td>
                  <td>
                    <span className={`badge ${
                      entry.action.includes('SALE') ? 'badge-success' :
                      entry.action.includes('DISPATCH') ? 'badge-primary' :
                      entry.action.includes('AUDIT') ? 'badge-warning' : 'badge-neutral'
                    } text-[11px]`}>
                      {entry.action}
                    </span>
                  </td>
                  <td className="text-xs text-gray-800 max-w-md">
                    {entry.details}
                  </td>
                  <td className="text-xs font-semibold text-navy-900">
                    {entry.performedBy}
                  </td>
                  <td className="font-mono text-xs text-gray-400">
                    {entry.ipAddress}
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
