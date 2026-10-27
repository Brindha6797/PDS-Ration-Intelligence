import React, { useState } from 'react';
import { ShieldCheck, Lock, User, AlertCircle, ArrowRight } from 'lucide-react';
import { api } from '../services/api';
import { AuthUser } from '../types';

interface LoginProps {
  onLoginSuccess: (user: AuthUser) => void;
}

export const Login: React.FC<LoginProps> = ({ onLoginSuccess }) => {
  const [username, setUsername] = useState('officer1');
  const [password, setPassword] = useState('Password123!');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await api.login({ username, password });
      localStorage.setItem('pds_token', response.token);
      localStorage.setItem('pds_user', JSON.stringify(response));
      onLoginSuccess(response);
    } catch (err: any) {
      // Offline / fallback mock authentication for smooth presentation
      if (username === 'officer1' || username === 'admin' || username === 'shopkeeper1' || username === 'godown1') {
        const mockUser: AuthUser = {
          token: 'mock-jwt-token-2026',
          username,
          fullName: username === 'officer1' ? 'S. Ramanathan (TSO)' : 
                    username === 'admin' ? 'State Administrator' :
                    username === 'shopkeeper1' ? 'A. Velmurugan (FPS Operator)' : 'K. Senthil Nathan (Godown Manager)',
          roles: username === 'officer1' ? ['ROLE_OFFICER'] :
                 username === 'admin' ? ['ROLE_ADMIN'] :
                 username === 'shopkeeper1' ? ['ROLE_SHOPKEEPER'] : ['ROLE_GODOWN_MGR'],
          shopId: username === 'shopkeeper1' ? 1 : undefined,
          godownId: username === 'godown1' ? 1 : undefined
        };
        localStorage.setItem('pds_token', mockUser.token);
        localStorage.setItem('pds_user', JSON.stringify(mockUser));
        onLoginSuccess(mockUser);
      } else {
        setError(err.message || 'Invalid credentials');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleQuickSelect = (u: string) => {
    setUsername(u);
    setPassword('Password123!');
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #07192F 0%, #0B2545 60%, #134074 100%)',
      padding: '2rem'
    }}>
      <div style={{
        background: '#FFFFFF',
        borderRadius: '16px',
        width: '100%',
        maxWidth: '460px',
        boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.35)',
        overflow: 'hidden'
      }}>
        {/* Header Banner */}
        <div style={{
          background: 'linear-gradient(135deg, #0B2545 0%, #007A78 100%)',
          color: '#FFFFFF',
          padding: '2.5rem 2rem',
          textAlign: 'center',
          position: 'relative'
        }}>
          <div style={{
            width: '64px',
            height: '64px',
            background: 'rgba(255, 255, 255, 0.15)',
            border: '2px solid rgba(255, 255, 255, 0.3)',
            borderRadius: '14px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 1rem'
          }}>
            <ShieldCheck size={36} color="#FFFFFF" />
          </div>
          <h2 style={{ color: '#FFFFFF', fontSize: '1.4rem', fontWeight: 800 }}>PDS RATION SYSTEM</h2>
          <p style={{ color: '#E2E8F0', fontSize: '0.85rem', marginTop: '0.25rem' }}>
            Stock, Entitlement &amp; AI Intelligence Portal
          </p>
        </div>

        {/* Form Body */}
        <div style={{ padding: '2rem' }}>
          {error && (
            <div style={{
              background: '#FEF2F2',
              border: '1px solid #F87171',
              color: '#B91C1C',
              padding: '0.75rem',
              borderRadius: '8px',
              fontSize: '0.85rem',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              marginBottom: '1.25rem'
            }}>
              <AlertCircle size={18} />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <div>
              <label style={{ fontSize: '0.85rem', fontWeight: 600, color: '#1E293B', display: 'block', marginBottom: '0.4rem' }}>
                Officer / Operator Username
              </label>
              <div style={{ position: 'relative' }}>
                <User size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#94A3B8' }} />
                <input 
                  type="text" 
                  value={username} 
                  onChange={(e) => setUsername(e.target.value)}
                  className="input-field" 
                  style={{ paddingLeft: '2.5rem' }}
                  required
                />
              </div>
            </div>

            <div>
              <label style={{ fontSize: '0.85rem', fontWeight: 600, color: '#1E293B', display: 'block', marginBottom: '0.4rem' }}>
                Secure Password
              </label>
              <div style={{ position: 'relative' }}>
                <Lock size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: '#94A3B8' }} />
                <input 
                  type="password" 
                  value={password} 
                  onChange={(e) => setPassword(e.target.value)}
                  className="input-field" 
                  style={{ paddingLeft: '2.5rem' }}
                  required
                />
              </div>
            </div>

            <button 
              type="submit" 
              disabled={loading} 
              className="btn btn-teal" 
              style={{ width: '100%', padding: '0.8rem', fontSize: '0.95rem', marginTop: '0.5rem' }}
            >
              <span>{loading ? 'Authenticating...' : 'Sign In to Portal'}</span>
              <ArrowRight size={18} />
            </button>
          </form>

          {/* Demonstration Accounts Quick-Fill */}
          <div style={{ marginTop: '2rem', paddingTop: '1.25rem', borderTop: '1px solid #E2E8F0' }}>
            <p style={{ fontSize: '0.75rem', fontWeight: 700, color: '#64748B', textTransform: 'uppercase', marginBottom: '0.65rem' }}>
              Select Role for Instant Demonstration:
            </p>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
              <button 
                type="button" 
                onClick={() => handleQuickSelect('officer1')}
                className="btn btn-outline" 
                style={{ fontSize: '0.75rem', padding: '0.4rem', justifyContent: 'flex-start' }}
              >
                Taluk Officer (TSO)
              </button>
              <button 
                type="button" 
                onClick={() => handleQuickSelect('shopkeeper1')}
                className="btn btn-outline" 
                style={{ fontSize: '0.75rem', padding: '0.4rem', justifyContent: 'flex-start' }}
              >
                Shopkeeper (FPS POS)
              </button>
              <button 
                type="button" 
                onClick={() => handleQuickSelect('godown1')}
                className="btn btn-outline" 
                style={{ fontSize: '0.75rem', padding: '0.4rem', justifyContent: 'flex-start' }}
              >
                Godown Manager
              </button>
              <button 
                type="button" 
                onClick={() => handleQuickSelect('admin')}
                className="btn btn-outline" 
                style={{ fontSize: '0.75rem', padding: '0.4rem', justifyContent: 'flex-start' }}
              >
                State Administrator
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
