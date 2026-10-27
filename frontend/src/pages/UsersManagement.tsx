import React, { useState } from 'react';
import { 
  Users, 
  ShieldCheck, 
  UserPlus, 
  CheckCircle, 
  Key, 
  Building, 
  Search 
} from 'lucide-react';

interface SystemUser {
  userId: number;
  username: string;
  fullName: string;
  email: string;
  role: string;
  assignedEntity: string;
  status: string;
  lastLogin: string;
}

export const UsersManagement: React.FC = () => {
  const [users] = useState<SystemUser[]>([
    {
      userId: 1,
      username: 'officer_pds',
      fullName: 'R. K. Ramanathan (Tahsildar)',
      email: 'tahsildar.taluk@tn.pds.gov.in',
      role: 'ROLE_TALUK_OFFICER',
      assignedEntity: 'Taluk Civil Supplies HQ',
      status: 'ACTIVE',
      lastLogin: '2026-10-27 10:14 AM'
    },
    {
      userId: 2,
      username: 'shop_001',
      fullName: 'M. Selvam (FPS #101 Dealer)',
      email: 'fps101.dealer@tn.pds.gov.in',
      role: 'ROLE_SHOPKEEPER',
      assignedEntity: 'FPS #101 - Gandhi Nagar',
      status: 'ACTIVE',
      lastLogin: '2026-10-27 08:30 AM'
    },
    {
      userId: 3,
      username: 'godown_01',
      fullName: 'S. Anbarasan (Depot Manager)',
      email: 'depot1.manager@tn.pds.gov.in',
      role: 'ROLE_GODOWN_MANAGER',
      assignedEntity: 'Central Taluk Buffer Godown',
      status: 'ACTIVE',
      lastLogin: '2026-10-27 07:45 AM'
    },
    {
      userId: 4,
      username: 'admin',
      fullName: 'System Security Administrator',
      email: 'pds.admin@tn.gov.in',
      role: 'ROLE_ADMIN',
      assignedEntity: 'State Data Center (SDC)',
      status: 'ACTIVE',
      lastLogin: '2026-10-27 11:55 AM'
    }
  ]);

  const [searchTerm, setSearchTerm] = useState('');

  const filtered = users.filter(u => 
    u.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.fullName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.role.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-navy-900 flex items-center gap-2">
            <Users className="text-teal-600" /> User & Role-Based Access Control (RBAC)
          </h1>
          <p className="text-gray-500 text-sm">
            Statutory role assignments, identity access governance, and field assignment binding
          </p>
        </div>
        <button 
          onClick={() => alert('New User Registration Modal: Enter official employee ID, designated role, and assigned PDS entity.')}
          className="btn btn-primary flex items-center gap-2"
        >
          <UserPlus size={16} /> Create User Account
        </button>
      </div>

      {/* Role Matrix Summary */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div className="card p-4 border-l-4 border-l-navy-900">
          <div className="text-xs uppercase text-gray-500 font-bold">Taluk Officers</div>
          <div className="text-xl font-bold text-navy-900 mt-1">ROLE_TALUK_OFFICER</div>
          <p className="text-[11px] text-gray-500 mt-1">Allotment approval, grievance resolution, AI radar audits</p>
        </div>
        <div className="card p-4 border-l-4 border-l-teal-600">
          <div className="text-xs uppercase text-gray-500 font-bold">Shop Dealers</div>
          <div className="text-xl font-bold text-teal-800 mt-1">ROLE_SHOPKEEPER</div>
          <p className="text-[11px] text-gray-500 mt-1">POS sales terminal, stock receipt confirmation, daily weighment</p>
        </div>
        <div className="card p-4 border-l-4 border-l-amber-500">
          <div className="text-xs uppercase text-gray-500 font-bold">Godown Managers</div>
          <div className="text-xl font-bold text-amber-700 mt-1">ROLE_GODOWN_MANAGER</div>
          <p className="text-[11px] text-gray-500 mt-1">Truck consignment dispatch, bulk buffer intake, indent tracking</p>
        </div>
        <div className="card p-4 border-l-4 border-l-red-600">
          <div className="text-xs uppercase text-gray-500 font-bold">System Administrators</div>
          <div className="text-xl font-bold text-red-700 mt-1">ROLE_ADMIN</div>
          <p className="text-[11px] text-gray-500 mt-1">Welfare card category rules, master commodities, audit logs</p>
        </div>
      </div>

      {/* Search */}
      <div className="card p-4 mb-6">
        <div className="relative max-w-md">
          <Search className="absolute left-3 top-2.5 text-gray-400" size={18} />
          <input 
            type="text" 
            placeholder="Search users by name, login, or role..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="input pl-10"
          />
        </div>
      </div>

      {/* Users Table */}
      <div className="card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="table">
            <thead>
              <tr>
                <th>Username</th>
                <th>Full Name & Email</th>
                <th>Assigned Role</th>
                <th>Designated Facility / Jurisdiction</th>
                <th>Account Status</th>
                <th>Last Active Session</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map(u => (
                <tr key={u.userId}>
                  <td className="font-bold text-navy-900 font-mono text-xs">{u.username}</td>
                  <td>
                    <div className="font-medium text-gray-900">{u.fullName}</div>
                    <div className="text-xs text-gray-500">{u.email}</div>
                  </td>
                  <td>
                    <span className="badge badge-primary font-mono text-[11px]">
                      {u.role}
                    </span>
                  </td>
                  <td className="text-xs text-gray-700 flex items-center gap-1.5 pt-4">
                    <Building size={14} className="text-gray-400" />
                    {u.assignedEntity}
                  </td>
                  <td>
                    <span className="badge badge-success text-xs">
                      {u.status}
                    </span>
                  </td>
                  <td className="text-xs text-gray-500">{u.lastLogin}</td>
                  <td>
                    <button 
                      onClick={() => alert(`Password reset token generated for ${u.username}`)}
                      className="btn btn-outline text-xs py-1 px-2 flex items-center gap-1"
                    >
                      <Key size={12} /> Reset Credentials
                    </button>
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
