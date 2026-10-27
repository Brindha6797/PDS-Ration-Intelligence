import React, { useState } from 'react';
import { Navbar } from './components/Navbar';
import { Sidebar } from './components/Sidebar';
import { AuthUser } from './types';

// Page Imports
import { Login } from './pages/Login';
import { Dashboard } from './pages/Dashboard';
import { CardHolders } from './pages/CardHolders';
import { FamilyMembers } from './pages/FamilyMembers';
import { CardTypes } from './pages/CardTypes';
import { Entitlements } from './pages/Entitlements';
import { Shops } from './pages/Shops';
import { Commodities } from './pages/Commodities';
import { Godowns } from './pages/Godowns';
import { Allotments } from './pages/Allotments';
import { Dispatches } from './pages/Dispatches';
import { SalesTerminal } from './pages/SalesTerminal';
import { Reconciliation } from './pages/Reconciliation';
import { Complaints } from './pages/Complaints';
import { AiAnomalies } from './pages/AiAnomalies';
import { AiForecast } from './pages/AiForecast';
import { AiComplaintClassifier } from './pages/AiComplaintClassifier';
import { AiAssistant } from './pages/AiAssistant';
import { Reports } from './pages/Reports';
import { UsersManagement } from './pages/UsersManagement';
import { AuditLogs } from './pages/AuditLogs';

export const App: React.FC = () => {
  // Default session for seamless presentation demonstration
  const [currentUser, setCurrentUser] = useState<AuthUser | null>(() => {
    const saved = localStorage.getItem('pds_user');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch (_) {}
    }
    return {
      userId: 1,
      username: 'officer_pds',
      fullName: 'R. K. Ramanathan (Tahsildar)',
      token: 'jwt-officer-session-token',
      roles: ['ROLE_TALUK_OFFICER']
    };
  });

  const [activeTab, setActiveTab] = useState<string>('dashboard');

  const handleLogin = (user: AuthUser) => {
    setCurrentUser(user);
    localStorage.setItem('pds_user', JSON.stringify(user));
    localStorage.setItem('pds_token', user.token);
    setActiveTab('dashboard');
  };

  const handleLogout = () => {
    setCurrentUser(null);
    localStorage.removeItem('pds_user');
    localStorage.removeItem('pds_token');
  };

  // If not logged in, render the login portal
  if (!currentUser) {
    return <Login onLoginSuccess={handleLogin} />;
  }

  // Router switch rendering the requested active view
  const renderContent = () => {
    switch (activeTab) {
      case 'dashboard':
        return <Dashboard onNavigate={(tab) => setActiveTab(tab)} />;
      case 'cards':
        return <CardHolders />;
      case 'members':
        return <FamilyMembers />;
      case 'card-types':
        return <CardTypes />;
      case 'entitlements':
        return <Entitlements />;
      case 'shops':
        return <Shops />;
      case 'commodities':
        return <Commodities />;
      case 'godowns':
        return <Godowns />;
      case 'allotments':
        return <Allotments />;
      case 'dispatches':
        return <Dispatches />;
      case 'sales-terminal':
        return <SalesTerminal />;
      case 'reconciliation':
        return <Reconciliation />;
      case 'complaints':
        return <Complaints />;
      case 'ai-anomalies':
        return <AiAnomalies />;
      case 'ai-forecast':
        return <AiForecast />;
      case 'ai-classifier':
        return <AiComplaintClassifier />;
      case 'ai-assistant':
        return <AiAssistant />;
      case 'reports':
        return <Reports />;
      case 'users':
        return <UsersManagement />;
      case 'audit-logs':
        return <AuditLogs />;
      default:
        return <Dashboard onNavigate={(tab) => setActiveTab(tab)} />;
    }
  };

  return (
    <div className="app-shell">
      <Navbar user={currentUser} onLogout={handleLogout} />
      <div className="app-body">
        <Sidebar 
          currentTab={activeTab} 
          onSelectTab={setActiveTab} 
          userRole={currentUser.roles[0]} 
        />
        <main className="app-main">
          {renderContent()}
        </main>
      </div>
    </div>
  );
};

export default App;
