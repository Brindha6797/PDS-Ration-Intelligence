import React, { useState, useEffect } from 'react';
import { 
  AlertCircle, 
  CheckCircle2, 
  Clock, 
  Search, 
  Plus, 
  Sparkles, 
  MessageSquare, 
  UserCheck, 
  FileText 
} from 'lucide-react';
import { complaintApi, aiApi, shopApi } from '../services/api';
import { Complaint, Shop } from '../types';

export const Complaints: React.FC = () => {
  const [complaints, setComplaints] = useState<Complaint[]>([]);
  const [shops, setShops] = useState<Shop[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  
  // New Complaint Modal
  const [showModal, setShowModal] = useState(false);
  const [newCardNumber, setNewCardNumber] = useState('');
  const [newShopId, setNewShopId] = useState<number | ''>('');
  const [newCategory, setNewCategory] = useState('QUANTITY_DISCREPANCY');
  const [newDescription, setNewDescription] = useState('');
  const [aiSuggestions, setAiSuggestions] = useState<{
    category: string;
    priority: string;
    confidence: number;
    explanation: string;
  } | null>(null);
  const [analyzingText, setAnalyzingText] = useState(false);

  // Update Status Modal
  const [selectedComplaint, setSelectedComplaint] = useState<Complaint | null>(null);
  const [updateStatus, setUpdateStatus] = useState('RESOLVED');
  const [officerNotes, setOfficerNotes] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [compRes, shopRes] = await Promise.all([
        complaintApi.getAll(),
        shopApi.getAll()
      ]);
      setComplaints(compRes.data);
      setShops(shopRes.data);
    } catch (err) {
      console.error('Failed to load complaints data', err);
    } finally {
      setLoading(false);
    }
  };

  const handleAiAnalyze = async () => {
    if (!newDescription.trim()) return;
    setAnalyzingText(true);
    try {
      const res = await aiApi.classifyComplaint(newDescription);
      setAiSuggestions({
        category: res.data.predicted_category,
        priority: res.data.suggested_priority,
        confidence: res.data.confidence,
        explanation: res.data.explanation
      });
      setNewCategory(res.data.predicted_category);
    } catch (err) {
      console.error('AI classification error', err);
    } finally {
      setAnalyzingText(false);
    }
  };

  const handleRegisterComplaint = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newShopId || !newDescription) return;

    try {
      await complaintApi.create({
        cardNumber: newCardNumber || undefined,
        shopId: Number(newShopId),
        category: newCategory,
        description: newDescription,
        priority: aiSuggestions?.priority || 'MEDIUM'
      });
      setShowModal(false);
      resetForm();
      loadData();
    } catch (err: any) {
      alert('Failed to register complaint: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleUpdateStatus = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedComplaint) return;

    try {
      await complaintApi.updateStatus(selectedComplaint.complaintId, {
        status: updateStatus,
        resolutionNotes: officerNotes,
        assignedOfficer: 'Taluk Civil Supplies Officer'
      });
      setSelectedComplaint(null);
      setOfficerNotes('');
      loadData();
    } catch (err: any) {
      alert('Failed to update status: ' + (err.response?.data?.message || err.message));
    }
  };

  const resetForm = () => {
    setNewCardNumber('');
    setNewShopId('');
    setNewCategory('QUANTITY_DISCREPANCY');
    setNewDescription('');
    setAiSuggestions(null);
  };

  const filtered = complaints.filter(c => {
    const matchesSearch = c.complaintNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          c.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          (c.shopName && c.shopName.toLowerCase().includes(searchTerm.toLowerCase()));
    const matchesStatus = statusFilter === 'ALL' || c.status === statusFilter;
    const matchesCategory = categoryFilter === 'ALL' || c.category === categoryFilter;
    return matchesSearch && matchesStatus && matchesCategory;
  });

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-navy-900">Grievance & Complaint Management</h1>
          <p className="text-gray-500 text-sm">Citizen grievance redressal, shop inspection tracking, and AI triage</p>
        </div>
        <button 
          onClick={() => { resetForm(); setShowModal(true); }}
          className="btn btn-primary flex items-center gap-2"
        >
          <Plus size={16} /> Register New Complaint
        </button>
      </div>

      {/* Summary KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div className="kpi-card border-l-4 border-l-amber-500">
          <div className="flex justify-between items-center">
            <div>
              <p className="text-xs uppercase tracking-wider text-gray-500 font-semibold">Total Complaints</p>
              <h3 className="text-2xl font-bold text-navy-900 mt-1">{complaints.length}</h3>
            </div>
            <MessageSquare className="text-amber-500" size={24} />
          </div>
        </div>
        <div className="kpi-card border-l-4 border-l-red-500">
          <div className="flex justify-between items-center">
            <div>
              <p className="text-xs uppercase tracking-wider text-gray-500 font-semibold">Under Investigation</p>
              <h3 className="text-2xl font-bold text-red-600 mt-1">
                {complaints.filter(c => c.status === 'UNDER_INVESTIGATION' || c.status === 'SUBMITTED').length}
              </h3>
            </div>
            <Clock className="text-red-500" size={24} />
          </div>
        </div>
        <div className="kpi-card border-l-4 border-l-emerald-500">
          <div className="flex justify-between items-center">
            <div>
              <p className="text-xs uppercase tracking-wider text-gray-500 font-semibold">Resolved Cases</p>
              <h3 className="text-2xl font-bold text-emerald-600 mt-1">
                {complaints.filter(c => c.status === 'RESOLVED').length}
              </h3>
            </div>
            <CheckCircle2 className="text-emerald-500" size={24} />
          </div>
        </div>
        <div className="kpi-card border-l-4 border-l-teal-600">
          <div className="flex justify-between items-center">
            <div>
              <p className="text-xs uppercase tracking-wider text-gray-500 font-semibold">AI Triage Active</p>
              <h3 className="text-2xl font-bold text-teal-700 mt-1">100%</h3>
            </div>
            <Sparkles className="text-teal-600" size={24} />
          </div>
        </div>
      </div>

      {/* Filters */}
      <div className="card p-4 mb-6">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-2.5 text-gray-400" size={18} />
            <input 
              type="text" 
              placeholder="Search reference, description, shop..." 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="input pl-10"
            />
          </div>
          <div>
            <select 
              value={statusFilter} 
              onChange={(e) => setStatusFilter(e.target.value)}
              className="input"
            >
              <option value="ALL">All Statuses</option>
              <option value="SUBMITTED">SUBMITTED</option>
              <option value="UNDER_INVESTIGATION">UNDER INVESTIGATION</option>
              <option value="RESOLVED">RESOLVED</option>
              <option value="REJECTED">REJECTED</option>
            </select>
          </div>
          <div>
            <select 
              value={categoryFilter} 
              onChange={(e) => setCategoryFilter(e.target.value)}
              className="input"
            >
              <option value="ALL">All Categories</option>
              <option value="QUANTITY_DISCREPANCY">Quantity Discrepancy</option>
              <option value="STOCK_UNAVAILABLE">Stock Unavailable</option>
              <option value="DELAYED_DISTRIBUTION">Delayed Distribution</option>
              <option value="INCORRECT_ENTITLEMENT">Incorrect Entitlement</option>
              <option value="SHOP_RELATED">Shop Behavior / Timings</option>
              <option value="OTHER">Other Issues</option>
            </select>
          </div>
        </div>
      </div>

      {/* Complaints Table */}
      <div className="card overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-gray-500">Loading complaints register...</div>
        ) : filtered.length === 0 ? (
          <div className="p-8 text-center text-gray-500">No complaints matching current filters.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="table">
              <thead>
                <tr>
                  <th>Reference</th>
                  <th>Date</th>
                  <th>Shop & Card</th>
                  <th>Category</th>
                  <th>Description</th>
                  <th>Priority</th>
                  <th>Status</th>
                  <th>Assigned Officer</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map(c => (
                  <tr key={c.complaintId}>
                    <td className="font-semibold text-navy-900">{c.complaintNumber}</td>
                    <td className="text-xs text-gray-500">
                      {new Date(c.createdAt).toLocaleDateString()}
                    </td>
                    <td>
                      <div className="font-medium text-xs text-navy-800">{c.shopName || `Shop #${c.shopId}`}</div>
                      <div className="text-xs text-gray-500">{c.cardNumber || 'Anonymous/Direct'}</div>
                    </td>
                    <td>
                      <span className="text-xs px-2 py-0.5 bg-gray-100 rounded text-gray-700 font-medium">
                        {c.category.replace(/_/g, ' ')}
                      </span>
                    </td>
                    <td className="max-w-xs truncate text-xs text-gray-600" title={c.description}>
                      {c.description}
                    </td>
                    <td>
                      <span className={`badge ${
                        c.priority === 'CRITICAL' || c.priority === 'HIGH' ? 'badge-danger' : 
                        c.priority === 'MEDIUM' ? 'badge-warning' : 'badge-neutral'
                      }`}>
                        {c.priority}
                      </span>
                    </td>
                    <td>
                      <span className={`badge ${
                        c.status === 'RESOLVED' ? 'badge-success' :
                        c.status === 'UNDER_INVESTIGATION' ? 'badge-primary' : 'badge-warning'
                      }`}>
                        {c.status.replace(/_/g, ' ')}
                      </span>
                    </td>
                    <td className="text-xs text-gray-600">
                      {c.assignedOfficer || 'Unassigned'}
                    </td>
                    <td>
                      <button 
                        onClick={() => { setSelectedComplaint(c); setOfficerNotes(c.resolutionNotes || ''); }}
                        className="btn btn-outline text-xs py-1 px-2 flex items-center gap-1"
                      >
                        <UserCheck size={14} /> Update
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Register Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content max-w-xl">
            <h3 className="text-lg font-bold text-navy-900 mb-4 flex items-center gap-2">
              <FileText className="text-teal-600" /> Lodge Citizen Grievance
            </h3>
            <form onSubmit={handleRegisterComplaint}>
              <div className="grid grid-cols-2 gap-4 mb-4">
                <div>
                  <label className="label">Fair-Price Shop *</label>
                  <select 
                    value={newShopId} 
                    onChange={(e) => setNewShopId(Number(e.target.value))}
                    className="input"
                    required
                  >
                    <option value="">Select Shop</option>
                    {shops.map(s => (
                      <option key={s.shopId} value={s.shopId}>{s.shopCode} - {s.shopName}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="label">Ration Card (Optional)</label>
                  <input 
                    type="text" 
                    placeholder="e.g. TN-04-AAY-10001" 
                    value={newCardNumber}
                    onChange={(e) => setNewCardNumber(e.target.value)}
                    className="input"
                  />
                </div>
              </div>

              <div className="mb-4">
                <label className="label">Grievance Description (English or Tamil) *</label>
                <textarea 
                  rows={4}
                  placeholder="Describe the issue. (e.g., 'Shopkeeper gave 15kg rice instead of 20kg' or 'ரேஷன் கடையில் அரிசி இருப்பு இல்லை என்று கூறினார்கள்')"
                  value={newDescription}
                  onChange={(e) => setNewDescription(e.target.value)}
                  className="input"
                  required
                />
                <div className="flex justify-end mt-2">
                  <button 
                    type="button" 
                    onClick={handleAiAnalyze}
                    disabled={analyzingText || !newDescription.trim()}
                    className="btn btn-outline text-xs flex items-center gap-1.5 text-teal-700 border-teal-600 hover:bg-teal-50"
                  >
                    <Sparkles size={14} /> {analyzingText ? 'Analyzing...' : 'AI Category & Priority Triage'}
                  </button>
                </div>
              </div>

              {aiSuggestions && (
                <div className="p-3 bg-teal-50 border border-teal-200 rounded-md mb-4 text-xs">
                  <div className="flex items-center gap-1.5 font-bold text-teal-800 mb-1">
                    <Sparkles size={14} /> AI Recommendation:
                  </div>
                  <div className="grid grid-cols-2 gap-2 text-teal-900 mb-1">
                    <div>Category: <strong>{aiSuggestions.category.replace(/_/g, ' ')}</strong></div>
                    <div>Priority: <strong>{aiSuggestions.priority}</strong> ({(aiSuggestions.confidence * 100).toFixed(0)}% match)</div>
                  </div>
                  <p className="text-teal-700 italic">{aiSuggestions.explanation}</p>
                </div>
              )}

              <div className="mb-6">
                <label className="label">Classification Category *</label>
                <select 
                  value={newCategory} 
                  onChange={(e) => setNewCategory(e.target.value)}
                  className="input"
                  required
                >
                  <option value="QUANTITY_DISCREPANCY">Quantity Discrepancy (குறைந்த அளவு)</option>
                  <option value="STOCK_UNAVAILABLE">Stock Unavailable (இருப்பு இல்லை)</option>
                  <option value="DELAYED_DISTRIBUTION">Delayed Distribution (தாமதமான விநியோகம்)</option>
                  <option value="INCORRECT_ENTITLEMENT">Incorrect Entitlement (தவறான உரிமை)</option>
                  <option value="SHOP_RELATED">Shop Behavior / Timings</option>
                  <option value="OTHER">Other Issue</option>
                </select>
              </div>

              <div className="flex justify-end gap-3">
                <button 
                  type="button" 
                  onClick={() => setShowModal(false)}
                  className="btn btn-outline"
                >
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Submit Grievance
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Update Status Modal */}
      {selectedComplaint && (
        <div className="modal-overlay">
          <div className="modal-content max-w-lg">
            <h3 className="text-lg font-bold text-navy-900 mb-4 flex items-center gap-2">
              <UserCheck className="text-teal-600" /> Update Grievance #{selectedComplaint.complaintNumber}
            </h3>
            <form onSubmit={handleUpdateStatus}>
              <div className="p-3 bg-gray-50 rounded mb-4 text-xs">
                <div className="font-semibold text-navy-900 mb-1">Grievance Details:</div>
                <p className="text-gray-700">{selectedComplaint.description}</p>
              </div>

              <div className="mb-4">
                <label className="label">Update Status *</label>
                <select 
                  value={updateStatus} 
                  onChange={(e) => setUpdateStatus(e.target.value)}
                  className="input"
                  required
                >
                  <option value="UNDER_INVESTIGATION">UNDER INVESTIGATION</option>
                  <option value="RESOLVED">RESOLVED</option>
                  <option value="REJECTED">REJECTED</option>
                </select>
              </div>

              <div className="mb-6">
                <label className="label">Official Investigation / Resolution Notes *</label>
                <textarea 
                  rows={4}
                  placeholder="Detail the field inspection, weighment scale verification, stock reconciliation, or corrective action taken..."
                  value={officerNotes}
                  onChange={(e) => setOfficerNotes(e.target.value)}
                  className="input"
                  required
                />
              </div>

              <div className="flex justify-end gap-3">
                <button 
                  type="button" 
                  onClick={() => setSelectedComplaint(null)}
                  className="btn btn-outline"
                >
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Save Officer Action
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
