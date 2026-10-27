export interface CardType {
  cardTypeId: number;
  typeCode: string;
  typeName: string;
  description?: string;
  colorCode?: string;
  isActive: string;
}

export interface Commodity {
  commodityId: number;
  commodityCode: string;
  commodityName: string;
  measurementUnit: string;
  consumerRatePerUnit: number;
  governmentSubsidyPerUnit: number;
  bufferStockThreshold: number;
  isActive: string;
}

export interface Shop {
  shopId: number;
  shopCode: string;
  shopName: string;
  licenseNumber: string;
  operatorName: string;
  contactNumber: string;
  talukName: string;
  wardVillage: string;
  address: string;
  latitude?: number;
  longitude?: number;
  shopStatus: string;
}

export interface CardHolder {
  cardId: number;
  cardNumber: string;
  cardType: CardType;
  headOfFamilyName: string;
  aadhaarLast4: string;
  mobileNumber?: string;
  addressLine1: string;
  talukName: string;
  districtName: string;
  pincode: string;
  assignedShop: Shop;
  totalFamilyMembers: number;
  annualIncome?: number;
  cardStatus: string;
  effectiveDate: string;
}

export interface FamilyMember {
  memberId: number;
  memberName: string;
  relationshipToHead: string;
  gender: string;
  dateOfBirth: string;
  age?: number;
  aadhaarLast4: string;
  isActive: string;
}

export interface EntitlementRule {
  ruleId: number;
  cardType: CardType;
  commodity: Commodity;
  calcMethod: string;
  baseQuantity: number;
  perMemberQuantity: number;
  minFamilySize?: number;
  maxFamilySize?: number;
  maxQuantityCeiling?: number;
  effectiveStartDate: string;
  isActive: string;
}

export interface RemainingEntitlement {
  commodityId: number;
  commodityCode: string;
  commodityName: string;
  measurementUnit: string;
  entitledQuantity: number;
  distributedQuantity: number;
  remainingQuantity: number;
  consumerRatePerUnit: number;
  status: string;
}

export interface ShopStock {
  stockId: number;
  shop: Shop;
  commodity: Commodity;
  currentQuantity: number;
  openingMonthBalance: number;
  totalReceivedMonth: number;
  totalDistributedMonth: number;
  lastSaleDate?: string;
  lastReconciliationDate?: string;
}

export interface Godown {
  godownId: number;
  godownCode: string;
  godownName: string;
  talukName: string;
  storageCapacityMt: number;
  managerName: string;
  contactNumber: string;
  address: string;
}

export interface GodownStock {
  godownStockId: number;
  godown: Godown;
  commodity: Commodity;
  currentQuantity: number;
  reservedQuantity: number;
  lastReplenishedDate?: string;
}

export interface Allotment {
  allotmentId: number;
  shop: Shop;
  commodity: Commodity;
  allotmentYear: number;
  allotmentMonth: number;
  allocatedQuantity: number;
  dispatchedQuantity: number;
  receivedQuantity: number;
  allotmentStatus: string;
  approvedAt?: string;
}

export interface DispatchItem {
  dispatchItemId: number;
  commodity: Commodity;
  dispatchedQuantity: number;
  receivedQuantity?: number;
  discrepancyQuantity?: number;
}

export interface Dispatch {
  dispatchId: number;
  dispatchNumber: string;
  godown: Godown;
  shop: Shop;
  vehicleNumber: string;
  driverName: string;
  driverPhone?: string;
  dispatchStatus: string;
  dispatchedAt: string;
  receivedAt?: string;
  dispatchedBy: string;
  receivedBy?: string;
  remarks?: string;
  items: DispatchItem[];
}

export interface SaleItem {
  saleItemId?: number;
  commodity: Commodity;
  quantity: number;
  unitRate: number;
  totalPrice: number;
  subsidyAmount?: number;
}

export interface Sale {
  saleId: number;
  billNumber: string;
  shop: Shop;
  cardHolder: CardHolder;
  saleDate: string;
  totalAmount: number;
  paymentMode: string;
  authMode: string;
  saleStatus: string;
  items: SaleItem[];
}

export interface StockReconciliation {
  reconId: number;
  shop: Shop;
  commodity: Commodity;
  reconDate: string;
  recordedSystemStock: number;
  calculatedExpectedStock: number;
  physicalCountedStock: number;
  varianceQuantity: number;
  discrepancyPercentage: number;
  investigationStatus: string;
  officerRemarks?: string;
}

export interface ComplaintHistory {
  historyId: number;
  previousStatus?: string;
  newStatus: string;
  changedByUser: string;
  actionTaken: string;
  actionTimestamp: string;
  comments?: string;
}

export interface Complaint {
  complaintId: number;
  complaintRef: string;
  complaintNumber?: string; // alias for complaintRef
  cardHolder?: CardHolder;
  cardNumber?: string;
  shop?: Shop;
  shopId?: number;
  shopName?: string;
  category: string;
  description: string;
  language: string;
  priority: string;
  status: string;
  assignedOfficer?: string;
  resolutionNotes?: string;
  resolvedAt?: string;
  createdAt: string;
}

export interface DashboardStats {
  totalActiveCards: number;
  totalShops: number;
  lowStockShops: number;
  pendingAllotments: number;
  completedSalesCount: number;
  totalSalesRevenue: number;
  openComplaints: number;
  unresolvedReconciliations: number;
  godownRiceStockMt: number;
  godownWheatStockMt: number;
}

export interface AiAnomaly {
  shop_id?: number;
  shop_name?: string;
  commodity_name?: string;
  transaction_reference?: string;
  expected_quantity?: number | string;
  recorded_quantity?: number | string;
  difference?: number | string;
  alert_reason?: string;
  risk_level?: string;
  recommended_action?: string;
  shopCode?: string;
  shopName?: string;
  commodityName?: string;
  recordedStock?: number;
  physicalStock?: number;
  varianceQuantity?: number;
  discrepancyPercentage?: number;
  alertReason?: string;
  riskIndicator?: string;
  recommendedSteps?: string[];
}

export type AnomalyAlert = AiAnomaly;

export interface AiForecast {
  shop_id?: number;
  shop_name?: string;
  commodity_name?: string;
  forecast_period?: string;
  estimated_demand?: number | string;
  available_stock?: number | string;
  days_of_stock_remaining: number;
  urgency?: string;
  potential_shortage_alert?: string;
  recommended_replenishment?: string;
  shopCode?: string;
  shopName?: string;
  commodityName?: string;
  forecastPeriod?: string;
  estimatedDemand?: number;
  availableStock?: number;
  dailyRunRate?: number;
  estimatedDaysRemaining?: number;
  shortageRiskLevel?: string;
  explanation?: string;
}

export type DemandForecast = AiForecast;

export interface AiComplaintClassification {
  detectedCategory: string;
  confidenceScore: number;
  suggestedPriority: string;
  languageDetected: string;
  sentiment: string;
  extractedKeywords: string[];
  recommendedAction: string;
}

export interface AiAssistantResponse {
  answer: string;
  queryIntent: string;
  tabularData?: any[];
  keyInsights?: string[];
  safetyNotice: string;
}

export interface AuthUser {
  token: string;
  username: string;
  fullName: string;
  roles: string[];
  shopId?: number;
  godownId?: number;
}

