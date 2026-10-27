import React from 'react';
import { 
  LayoutDashboard, CreditCard, Users, Layers, Award,
  Store, Package, Warehouse, CalendarCheck, Truck, ShoppingCart,
  Scale, MessageSquare, AlertTriangle, TrendingUp, Sparkles,
  Bot, BarChart3, ShieldAlert, History
} from 'lucide-react';

interface SidebarProps {
  currentTab: string;
  onSelectTab: (tab: string) => void;
  userRole?: string;
}

export const Sidebar: React.FC<SidebarProps> = ({ currentTab, onSelectTab, userRole }) => {
  const menuItems = [
    { section: 'Overview' },
    { id: 'dashboard', label: 'Taluk Dashboard', icon: LayoutDashboard },
    
    { section: 'Beneficiary & Quotas' },
    { id: 'cards', label: 'Ration Cards', icon: CreditCard },
    { id: 'members', label: 'Family Members', icon: Users },
    { id: 'card-types', label: 'Card Categories', icon: Layers },
    { id: 'entitlements', label: 'Entitlements Ledger', icon: Award },

    { section: 'Supply Chain & Depot' },
    { id: 'shops', label: 'Fair-Price Shops', icon: Store },
    { id: 'commodities', label: 'Subsidized Goods', icon: Package },
    { id: 'godowns', label: 'Godown Warehouses', icon: Warehouse },
    { id: 'allotments', label: 'Shop Allotments', icon: CalendarCheck },
    { id: 'dispatches', label: 'Dispatches & Inward', icon: Truck },

    { section: 'Transactions & POS' },
    { id: 'sales-terminal', label: 'POS Distribution Terminal', icon: ShoppingCart },
    { id: 'reconciliation', label: 'Stock Reconciliation', icon: Scale },
    { id: 'complaints', label: 'Grievance Redressal', icon: MessageSquare },

    { section: 'AI Intelligence Engine' },
    { id: 'ai-anomalies', label: 'Stock Anomaly Radar', icon: AlertTriangle },
    { id: 'ai-forecast', label: 'Demand Forecasting', icon: TrendingUp },
    { id: 'ai-classifier', label: 'AI Complaint Triage', icon: Sparkles },
    { id: 'ai-assistant', label: 'Officer Assistant Q&A', icon: Bot },

    { section: 'Governance & Audits' },
    { id: 'reports', label: 'Analytical Reports', icon: BarChart3 },
    { id: 'users', label: 'Users & Permissions', icon: ShieldAlert },
    { id: 'audit-logs', label: 'System Audit History', icon: History }
  ];

  return (
    <aside className="app-sidebar">
      <div className="sidebar-menu">
        {menuItems.map((item, idx) => {
          if (item.section) {
            return (
              <div key={idx} className="sidebar-category">
                {item.section}
              </div>
            );
          }
          const Icon = item.icon!;
          const isActive = currentTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onSelectTab(item.id!)}
              className={`nav-link-btn ${isActive ? 'active' : ''}`}
            >
              <Icon size={18} />
              <span>{item.label}</span>
            </button>
          );
        })}
      </div>
    </aside>
  );
};
