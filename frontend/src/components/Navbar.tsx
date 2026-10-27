import React from 'react';
import { ShieldCheck, User as UserIcon, LogOut, Clock } from 'lucide-react';
import { AuthUser } from '../types';

interface NavbarProps {
  user: AuthUser | null;
  onLogout: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ user, onLogout }) => {
  const currentDate = new Date().toLocaleDateString('en-IN', {
    weekday: 'short',
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });

  return (
    <header className="gov-topbar">
      <div className="gov-brand">
        <div className="gov-emblem">
          <ShieldCheck size={28} className="text-white" />
        </div>
        <div className="gov-title">
          <h2>PUBLIC DISTRIBUTION SYSTEM (PDS)</h2>
          <p>DEPARTMENT OF CIVIL SUPPLIES &amp; CONSUMER PROTECTION • GOVT OF TAMIL NADU</p>
        </div>
      </div>

      <div className="gov-user-panel">
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: '#94A3B8', fontSize: '0.85rem' }}>
          <Clock size={16} />
          <span>{currentDate}</span>
        </div>

        {user && (
          <>
            <div className="user-badge">
              <UserIcon size={14} />
              <span>{user.fullName} ({user.roles[0]?.replace('ROLE_', '')})</span>
            </div>
            <button 
              onClick={onLogout} 
              className="btn btn-outline" 
              style={{ color: '#FFFFFF', borderColor: 'rgba(255,255,255,0.3)', padding: '0.4rem 0.85rem' }}
              title="Logout"
            >
              <LogOut size={16} />
              <span>Logout</span>
            </button>
          </>
        )}
      </div>
    </header>
  );
};
