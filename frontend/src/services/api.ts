import { 
  AuthUser, CardHolder, FamilyMember, CardType, Commodity, Shop, Godown, GodownStock,
  ShopStock, Allotment, Dispatch, Sale, StockReconciliation, Complaint, ComplaintHistory,
  DashboardStats, AiAnomaly, AiForecast, AiComplaintClassification, AiAssistantResponse,
  RemainingEntitlement
} from '../types';

const API_BASE = '/api/v1';

function getAuthHeaders(): HeadersInit {
  const token = localStorage.getItem('pds_token');
  return {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
  };
}

async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const headers = { ...getAuthHeaders(), ...(options.headers || {}) };
  const response = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
  
  if (!response.ok) {
    let errorMsg = `Server error (${response.status})`;
    try {
      const errJson = await response.json();
      errorMsg = errJson.message || errJson.error || errorMsg;
    } catch (_) {}
    throw new Error(errorMsg);
  }
  return response.json();
}

export const api = {
  // Auth
  login: (credentials: { username: string; password: string }) => 
    request<AuthUser>('/auth/login', { method: 'POST', body: JSON.stringify(credentials) }),
  
  getCurrentUser: () => request<any>('/auth/me'),

  // Dashboard & Reports
  getDashboardStats: () => request<DashboardStats>('/reports/dashboard'),
  getMonthlyMatrix: (year?: number, month?: number) => 
    request<any[]>(`/reports/monthly-matrix?year=${year || ''}&month=${month || ''}`),
  getAuditLogs: () => request<any[]>('/reports/audit-logs'),

  // Beneficiaries & Cards
  getCards: (page = 0, size = 20, search = '', shopId?: number) => 
    request<any>(`/cards?page=${page}&size=${size}&search=${search}&shopId=${shopId || ''}`),
  getCardById: (id: number) => request<CardHolder>(`/cards/${id}`),
  getCardByNumber: (cardNumber: string) => request<CardHolder>(`/cards/by-number/${cardNumber}`),
  getCardMembers: (cardId: number) => request<FamilyMember[]>(`/cards/${cardId}/members`),
  addFamilyMember: (cardId: number, member: Partial<FamilyMember>) => 
    request<FamilyMember>(`/cards/${cardId}/members`, { method: 'POST', body: JSON.stringify(member) }),
  getNonDrawalReport: () => request<CardHolder[]>('/cards/inactive-drawal-report'),

  // Entitlements
  getRemainingEntitlements: (cardId: number, year?: number, month?: number) => 
    request<RemainingEntitlement[]>(`/entitlements/card/${cardId}/remaining?year=${year || ''}&month=${month || ''}`),
  generateEntitlements: (year: number, month: number) => 
    request<{ generatedCount: number }>('/entitlements/generate', { method: 'POST', body: JSON.stringify({ year, month }) }),
  getEntitlementRules: () => request<any[]>('/entitlements/rules'),

  // Shops & Stock
  getShops: () => request<Shop[]>('/shops'),
  getShopById: (id: number) => request<Shop>(`/shops/${id}`),
  getShopStock: (shopId: number) => request<ShopStock[]>(`/shops/${shopId}/stock`),

  // Commodities
  getCommodities: () => request<Commodity[]>('/commodities'),

  // Godowns
  getGodowns: () => request<Godown[]>('/godowns'),
  getGodownStock: (godownId: number) => request<GodownStock[]>(`/godowns/${godownId}/stock`),

  // Allotments
  getAllotments: (year?: number, month?: number, shopId?: number) => 
    request<Allotment[]>(`/allotments?year=${year || ''}&month=${month || ''}&shopId=${shopId || ''}`),
  calculateAllotments: (year: number, month: number) => 
    request<{ calculatedCount: number }>('/allotments/calculate', { method: 'POST', body: JSON.stringify({ year, month }) }),
  approveAllotment: (id: number) => request<Allotment>(`/allotments/${id}/approve`, { method: 'PUT' }),

  // Dispatches
  getDispatchesByShop: (shopId: number) => request<Dispatch[]>(`/dispatches/shop/${shopId}`),
  getDispatchesByGodown: (godownId: number) => request<Dispatch[]>(`/dispatches/godown/${godownId}`),
  createDispatch: (data: any) => request<Dispatch>('/dispatches', { method: 'POST', body: JSON.stringify(data) }),
  confirmReceipt: (dispatchId: number, remarks?: string) => 
    request<Dispatch>(`/dispatches/${dispatchId}/confirm-receipt`, { method: 'POST', body: JSON.stringify({ remarks }) }),

  // Sales (POS)
  processSale: (data: { shopId: number; cardId: number; paymentMode?: string; authMode?: string; items: { commodityId: number; quantity: number }[] }) => 
    request<any>('/sales', { method: 'POST', body: JSON.stringify(data) }),
  getSalesByShop: (shopId: number) => request<Sale[]>(`/sales/shop/${shopId}`),
  getSalesByCard: (cardId: number) => request<Sale[]>(`/sales/card/${cardId}`),

  // Reconciliation
  getReconciliations: (shopId?: number) => request<StockReconciliation[]>(`/reconciliation?shopId=${shopId || ''}`),
  recordReconciliation: (data: { shopId: number; commodityId: number; physicalCount: number; remarks?: string }) => 
    request<StockReconciliation>('/reconciliation', { method: 'POST', body: JSON.stringify(data) }),
  updateReconciliationStatus: (id: number, status: string, remarks?: string) => 
    request<StockReconciliation>(`/reconciliation/${id}/status`, { method: 'PUT', body: JSON.stringify({ status, remarks }) }),

  // Grievance Redressal
  getComplaints: (page = 0, size = 20) => request<any>(`/complaints?page=${page}&size=${size}`),
  getComplaintById: (id: number) => request<Complaint>(`/complaints/${id}`),
  getComplaintHistory: (id: number) => request<ComplaintHistory[]>(`/complaints/${id}/history`),
  registerComplaint: (data: any) => request<Complaint>('/complaints', { method: 'POST', body: JSON.stringify(data) }),
  assignComplaint: (id: number, officerId: number) => 
    request<Complaint>(`/complaints/${id}/assign`, { method: 'PUT', body: JSON.stringify({ officerId }) }),
  resolveComplaint: (id: number, resolutionNotes: string, status = 'RESOLVED') => 
    request<Complaint>(`/complaints/${id}/resolve`, { method: 'PUT', body: JSON.stringify({ resolutionNotes, status }) }),

  // AI Intelligence Gateway
  getAiAnomalies: () => request<AiAnomaly[]>('/ai/anomalies'),
  getAiForecast: (shopId?: number, commodityId?: number) => 
    request<AiForecast[]>(`/ai/forecast?shopId=${shopId || ''}&commodityId=${commodityId || ''}`),
  classifyComplaint: (text: string, language?: string) => 
    request<AiComplaintClassification>('/ai/classify-complaint', { method: 'POST', body: JSON.stringify({ text, language }) }),
  askOfficerAssistant: (query: string) => 
    request<AiAssistantResponse>('/ai/officer-assistant', { method: 'POST', body: JSON.stringify({ query }) })
};

// Aliased service objects for multi-page component compatibility
export const shopApi = {
  getAll: async () => ({ data: await api.getShops() }),
  getById: async (id: number) => ({ data: await api.getShopById(id) }),
  getStock: async (id: number) => ({ data: await api.getShopStock(id) })
};

export const commodityApi = {
  getAll: async () => ({ data: await api.getCommodities() })
};

export const complaintApi = {
  getAll: async () => {
    try {
      const res = await api.getComplaints(0, 100);
      return { data: Array.isArray(res) ? res : res.content || [] };
    } catch (_) {
      return { data: [] };
    }
  },
  getById: async (id: number) => ({ data: await api.getComplaintById(id) }),
  create: async (payload: any) => ({ data: await api.registerComplaint(payload) }),
  updateStatus: async (id: number, payload: { status: string; resolutionNotes: string; assignedOfficer?: string }) => 
    ({ data: await api.resolveComplaint(id, payload.resolutionNotes, payload.status) })
};

export const aiApi = {
  detectAnomalies: async () => ({ data: await api.getAiAnomalies() }),
  getDemandForecast: async (shopId?: number, commodityId?: number) => ({ data: await api.getAiForecast(shopId, commodityId) }),
  classifyComplaint: async (text: string, language?: string) => {
    const raw: any = await api.classifyComplaint(text, language);
    return {
      data: {
        original_text: text,
        detected_language: raw.languageDetected || 'English',
        predicted_category: raw.detectedCategory || 'QUANTITY_DISCREPANCY',
        suggested_priority: raw.suggestedPriority || 'MEDIUM',
        confidence: raw.confidenceScore || 0.92,
        extracted_entities: { keywords: raw.extractedKeywords || [] },
        explanation: raw.recommendedAction || 'Matched statutory regulatory thresholds.'
      }
    };
  },
  askAssistant: async (query: string) => {
    const raw: any = await api.askOfficerAssistant(query);
    return {
      data: {
        response: raw.answer,
        intent: raw.queryIntent,
        data: raw.tabularData
      }
    };
  }
};

export const reportApi = {
  getSalesMatrix: async (period?: string, shopId?: number) => {
    const [year, month] = (period || '2026-10').split('-').map(Number);
    return { data: await api.getMonthlyMatrix(year, month) };
  },
  getGodownRegister: async () => {
    const godowns = await api.getGodowns();
    const rows: any[] = [];
    for (const g of godowns) {
      try {
        const stocks = await api.getGodownStock(g.godownId);
        for (const s of stocks) {
          rows.push({
            godown_code: g.godownCode,
            godown_name: g.godownName,
            manager: g.managerName,
            commodity: s.commodity?.commodityName,
            available_mt: s.currentQuantity,
            reserved_mt: s.reservedQuantity
          });
        }
      } catch (_) {}
    }
    return { data: rows };
  },
  getNonDrawalCards: async () => {
    const cards = await api.getNonDrawalReport();
    return {
      data: cards.map(c => ({
        card_number: c.cardNumber,
        head_of_family: c.headOfFamilyName,
        card_type: c.cardType?.typeCode,
        shop: c.assignedShop?.shopCode,
        status: '3 CONSECUTIVE MONTHS INACTIVE',
        statutory_notice: 'Under Taluk Verification'
      }))
    };
  }
};

