import React, { useState } from 'react';
import { 
  Bot, 
  Send, 
  ShieldCheck, 
  HelpCircle, 
  Sparkles, 
  Terminal, 
  Database, 
  FileText 
} from 'lucide-react';
import { aiApi } from '../services/api';

interface Message {
  id: string;
  sender: 'user' | 'assistant';
  text: string;
  timestamp: string;
  intent?: string;
  data?: any;
}

export const AiAssistant: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      sender: 'assistant',
      text: 'Good day, Officer. I am your Taluk Civil Supplies Decision Support Assistant. I provide real-time, read-only analytics on fair-price shops, stock variances, grievances, and replenishment requirements. How may I assist your inspection duties today?',
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    }
  ]);
  const [inputText, setInputText] = useState('');
  const [loading, setLoading] = useState(false);

  const suggestedQueries = [
    'Which shops have potential stock shortages?',
    'Which shops require urgent stock reconciliation?',
    'Which citizen complaints are currently unresolved?',
    'What are the monthly sales numbers for Shop 101?',
    'Which commodities need godown replenishment this week?'
  ];

  const handleSend = async (queryText: string) => {
    if (!queryText.trim()) return;

    const userMsg: Message = {
      id: Date.now().toString(),
      sender: 'user',
      text: queryText,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };

    setMessages(prev => [...prev, userMsg]);
    setInputText('');
    setLoading(true);

    try {
      const res = await aiApi.askAssistant(queryText);
      const assistantMsg: Message = {
        id: (Date.now() + 1).toString(),
        sender: 'assistant',
        text: res.data.response,
        intent: res.data.intent,
        data: res.data.data,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      };
      setMessages(prev => [...prev, assistantMsg]);
    } catch (err: any) {
      const errorMsg: Message = {
        id: (Date.now() + 1).toString(),
        sender: 'assistant',
        text: 'Apologies, I encountered an error while verifying authorized records: ' + (err.response?.data?.message || err.message),
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      };
      setMessages(prev => [...prev, errorMsg]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="h-[calc(100vh-140px)] flex flex-col">
      <div className="flex justify-between items-center mb-4">
        <div>
          <h1 className="text-2xl font-bold text-navy-900 flex items-center gap-2">
            <Bot className="text-teal-600" /> Civil Supplies Officer Decision Assistant
          </h1>
          <p className="text-gray-500 text-sm">
            Read-only natural language query interface for Taluk Civil Supplies Tahsildars and Field Inspectors
          </p>
        </div>
        <div className="flex items-center gap-2 bg-emerald-50 text-emerald-800 text-xs px-3 py-1.5 rounded-full border border-emerald-200">
          <ShieldCheck size={16} className="text-emerald-600" />
          <span>Strict Read-Only Sandboxed Scope Active</span>
        </div>
      </div>

      {/* Main Chat Container */}
      <div className="flex-1 card flex flex-col overflow-hidden">
        {/* Messages Stream */}
        <div className="flex-1 p-6 overflow-y-auto space-y-4 bg-gray-50/50">
          {messages.map(msg => (
            <div 
              key={msg.id} 
              className={`flex items-start gap-3 ${msg.sender === 'user' ? 'justify-end' : 'justify-start'}`}
            >
              {msg.sender === 'assistant' && (
                <div className="w-8 h-8 rounded-full bg-teal-600 flex items-center justify-center text-white flex-shrink-0">
                  <Bot size={18} />
                </div>
              )}

              <div className={`max-w-2xl rounded-lg p-4 text-sm leading-relaxed ${
                msg.sender === 'user' 
                  ? 'bg-navy-900 text-white rounded-br-none shadow-sm' 
                  : 'bg-white border border-gray-200 text-gray-800 rounded-bl-none shadow-sm'
              }`}>
                {msg.intent && (
                  <div className="text-[10px] uppercase font-bold tracking-wider text-teal-600 mb-1 flex items-center gap-1">
                    <Database size={10} /> Intent: {msg.intent.replace(/_/g, ' ')}
                  </div>
                )}
                
                <p className="whitespace-pre-line">{msg.text}</p>

                {/* Structured Output Preview if present */}
                {msg.data && Array.isArray(msg.data) && msg.data.length > 0 && (
                  <div className="mt-3 pt-3 border-t border-gray-100 overflow-x-auto">
                    <table className="text-xs w-full text-left">
                      <thead>
                        <tr className="text-gray-500 border-b">
                          {Object.keys(msg.data[0]).slice(0, 4).map((k, i) => (
                            <th key={i} className="py-1 px-2 font-medium capitalize">{k.replace(/_/g, ' ')}</th>
                          ))}
                        </tr>
                      </thead>
                      <tbody>
                        {msg.data.slice(0, 5).map((row: any, i: number) => (
                          <tr key={i} className="border-b border-gray-50 hover:bg-gray-50">
                            {Object.values(row).slice(0, 4).map((val: any, j: number) => (
                              <td key={j} className="py-1 px-2">{String(val)}</td>
                            ))}
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}

                <div className={`text-[10px] mt-2 text-right ${msg.sender === 'user' ? 'text-gray-300' : 'text-gray-400'}`}>
                  {msg.timestamp}
                </div>
              </div>

              {msg.sender === 'user' && (
                <div className="w-8 h-8 rounded-full bg-navy-800 flex items-center justify-center text-white flex-shrink-0">
                  <span className="text-xs font-bold">OFF</span>
                </div>
              )}
            </div>
          ))}

          {loading && (
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 rounded-full bg-teal-600 flex items-center justify-center text-white">
                <Bot size={18} />
              </div>
              <div className="bg-white border border-gray-200 rounded-lg p-3 text-xs text-gray-500 flex items-center gap-2">
                <Sparkles size={14} className="animate-spin text-teal-600" />
                Querying authorized civil supplies data and executing safety filters...
              </div>
            </div>
          )}
        </div>

        {/* Suggested Queries Bar */}
        <div className="p-3 bg-white border-t border-gray-200">
          <div className="text-xs text-gray-500 mb-2 font-medium flex items-center gap-1">
            <HelpCircle size={14} /> Recommended Inspection Queries:
          </div>
          <div className="flex flex-wrap gap-2">
            {suggestedQueries.map((q, idx) => (
              <button 
                key={idx}
                onClick={() => handleSend(q)}
                disabled={loading}
                className="text-xs px-2.5 py-1 bg-gray-100 hover:bg-teal-50 hover:text-teal-800 hover:border-teal-300 border border-transparent rounded transition text-gray-700"
              >
                {q}
              </button>
            ))}
          </div>
        </div>

        {/* Input Bar */}
        <div className="p-4 bg-white border-t border-gray-200">
          <form 
            onSubmit={(e) => { e.preventDefault(); handleSend(inputText); }}
            className="flex items-center gap-3"
          >
            <input 
              type="text" 
              placeholder="Ask questions about shop shortages, unfulfilled allotments, non-drawal cards, or grievances..."
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
              disabled={loading}
              className="input flex-1"
            />
            <button 
              type="submit" 
              disabled={loading || !inputText.trim()}
              className="btn btn-primary px-5 flex items-center gap-2"
            >
              <Send size={16} /> Send
            </button>
          </form>
          <div className="text-[11px] text-gray-400 mt-2 text-center">
            Zero-Mutation Guarantee: The assistant cannot modify cardholder records, stock levels, or transaction journals.
          </div>
        </div>
      </div>
    </div>
  );
};
