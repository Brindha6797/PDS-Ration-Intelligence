import React, { useState } from 'react';
import { 
  Languages, 
  Sparkles, 
  CheckCircle, 
  AlertTriangle, 
  ArrowRight, 
  BookOpen, 
  FileText 
} from 'lucide-react';
import { aiApi } from '../services/api';

export const AiComplaintClassifier: React.FC = () => {
  const [inputText, setInputText] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<{
    original_text: string;
    detected_language: string;
    predicted_category: string;
    suggested_priority: string;
    confidence: number;
    extracted_entities: Record<string, any>;
    explanation: string;
  } | null>(null);

  const sampleInputs = [
    {
      label: 'Tamil: Quantity Shortfall',
      text: 'ரேஷன் கடையில் 20 கிலோ அரிசிக்கு பதில் 15 கிலோ மட்டுமே கொடுத்தார்கள். எடை குறைவு.',
      lang: 'Tamil'
    },
    {
      label: 'Tamil: Out of Stock',
      text: 'கடைக்கு பலமுறை சென்றும் சர்க்கரை மற்றும் துவரம்பருப்பு இருப்பு இல்லை என்று கூறி திருப்பி அனுப்புகிறார்கள்.',
      lang: 'Tamil'
    },
    {
      label: 'Tamil: Irregular Timings',
      text: 'ரேஷன் கடை காலை 10 மணிக்கு திறப்பதில்லை, மதியமே மூடிவிடுகிறார்கள். பொதுமக்கள் நீண்ட நேரம் காத்து கிடக்கின்றனர்.',
      lang: 'Tamil'
    },
    {
      label: 'English: Overcharging / Denial',
      text: 'The shopkeeper refused to provide my full AAY quota of 35 kg rice and demanded extra 50 rupees.',
      lang: 'English'
    },
    {
      label: 'English: Point of Sale Machine Glitch',
      text: 'Biometric fingerprint scanner failed repeatedly and cardholder was denied ration distribution.',
      lang: 'English'
    }
  ];

  const handleClassify = async (textToClassify: string) => {
    if (!textToClassify.trim()) return;
    setLoading(true);
    try {
      const res = await aiApi.classifyComplaint(textToClassify);
      setResult(res.data);
    } catch (err: any) {
      alert('AI Classification failed: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-navy-900 flex items-center gap-2">
            <Languages className="text-teal-600" /> Multilingual Grievance NLP Classifier
          </h1>
          <p className="text-gray-500 text-sm">
            Automated sentiment, category, and urgency triage supporting Tamil (தமிழ்) and English citizen complaints
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Input & Tester Column */}
        <div className="lg:col-span-7 space-y-6">
          <div className="card p-5">
            <h3 className="font-bold text-navy-900 mb-3 flex items-center gap-2">
              <FileText size={18} className="text-teal-600" /> Enter Citizen Grievance Text
            </h3>
            
            <textarea 
              rows={5}
              placeholder="Type or paste grievance here in English or Tamil..."
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
              className="input mb-4"
            />

            <div className="flex justify-between items-center">
              <span className="text-xs text-gray-500">
                Supports Unicode Tamil text (UTF-8) & Standard English
              </span>
              <button 
                onClick={() => handleClassify(inputText)}
                disabled={loading || !inputText.trim()}
                className="btn btn-primary flex items-center gap-2"
              >
                <Sparkles size={16} /> {loading ? 'Analyzing NLP Features...' : 'Run Multilingual Triage'}
              </button>
            </div>
          </div>

          {/* Sample Presets */}
          <div className="card p-5">
            <h4 className="font-bold text-sm text-navy-900 mb-3 flex items-center gap-2">
              <BookOpen size={16} className="text-navy-700" /> Click to Test Pre-configured Grievance Samples:
            </h4>
            <div className="space-y-2">
              {sampleInputs.map((sample, idx) => (
                <button 
                  key={idx}
                  onClick={() => {
                    setInputText(sample.text);
                    handleClassify(sample.text);
                  }}
                  className="w-full text-left p-3 rounded border border-gray-200 hover:border-teal-500 hover:bg-teal-50/50 transition flex justify-between items-center group"
                >
                  <div>
                    <div className="flex items-center gap-2">
                      <span className={`badge ${sample.lang === 'Tamil' ? 'badge-primary' : 'badge-neutral'}`}>
                        {sample.lang}
                      </span>
                      <span className="text-xs font-bold text-gray-800">{sample.label}</span>
                    </div>
                    <p className="text-xs text-gray-600 mt-1 line-clamp-1">{sample.text}</p>
                  </div>
                  <ArrowRight size={16} className="text-gray-400 group-hover:text-teal-600 group-hover:translate-x-1 transition" />
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* AI Output Result Column */}
        <div className="lg:col-span-5">
          <div className="card p-5 h-full">
            <h3 className="font-bold text-navy-900 mb-4 flex items-center gap-2">
              <Sparkles className="text-teal-600" /> Classification & Triage Verdict
            </h3>

            {!result ? (
              <div className="h-64 flex flex-col items-center justify-center text-center text-gray-400 p-6 border-2 border-dashed border-gray-200 rounded-lg">
                <Languages size={40} className="mb-2 text-gray-300" />
                <p className="text-sm font-medium">No analysis performed yet</p>
                <p className="text-xs text-gray-500 mt-1">Submit text or pick a sample from the left panel to see real-time NLP classification.</p>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="p-4 bg-gray-50 rounded-lg border border-gray-200">
                  <div className="text-xs text-gray-500 mb-1">Detected Language:</div>
                  <div className="text-sm font-bold text-navy-900 flex items-center gap-2">
                    <span className="w-2.5 h-2.5 rounded-full bg-teal-500"></span>
                    {result.detected_language}
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div className="p-3 bg-teal-50 rounded-lg border border-teal-200">
                    <div className="text-xs text-teal-800 mb-1">Predicted Category:</div>
                    <div className="text-xs font-bold text-teal-900">
                      {result.predicted_category.replace(/_/g, ' ')}
                    </div>
                  </div>
                  <div className={`p-3 rounded-lg border ${
                    result.suggested_priority === 'CRITICAL' || result.suggested_priority === 'HIGH'
                      ? 'bg-red-50 border-red-200'
                      : 'bg-amber-50 border-amber-200'
                  }`}>
                    <div className="text-xs text-gray-700 mb-1">Suggested Priority:</div>
                    <div className={`text-xs font-bold ${
                      result.suggested_priority === 'CRITICAL' || result.suggested_priority === 'HIGH'
                        ? 'text-red-700'
                        : 'text-amber-800'
                    }`}>
                      {result.suggested_priority}
                    </div>
                  </div>
                </div>

                <div className="p-3 bg-white rounded border border-gray-200">
                  <div className="flex justify-between items-center text-xs mb-1">
                    <span className="font-semibold text-gray-700">Classification Confidence:</span>
                    <span className="font-bold text-navy-900">{(result.confidence * 100).toFixed(1)}%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      className="bg-teal-600 h-2 rounded-full" 
                      style={{ width: `${Math.min(result.confidence * 100, 100)}%` }}
                    ></div>
                  </div>
                </div>

                <div className="p-3 bg-blue-50/60 rounded border border-blue-200 text-xs">
                  <div className="font-bold text-blue-900 mb-1 flex items-center gap-1.5">
                    <CheckCircle size={14} className="text-blue-700" /> NLP Explanation:
                  </div>
                  <p className="text-blue-800 leading-relaxed">{result.explanation}</p>
                </div>

                {Object.keys(result.extracted_entities || {}).length > 0 && (
                  <div className="p-3 bg-gray-50 rounded border border-gray-200 text-xs">
                    <div className="font-semibold text-gray-700 mb-2">Extracted Entities & Keywords:</div>
                    <div className="flex flex-wrap gap-1.5">
                      {Object.entries(result.extracted_entities).map(([k, v], i) => (
                        <span key={i} className="px-2 py-0.5 bg-white border border-gray-300 rounded text-gray-700 text-xs font-mono">
                          {k}: {String(v)}
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
