import re
from typing import Dict, Any, List

class MultilingualComplaintClassifier:
    """
    Multilingual (English & Tamil) grievance text classification engine.
    Extracts semantic intent, classifies category, assesses urgency/priority,
    and provides explainable triage recommendations for civil supplies officers.
    """

    TAMIL_CHAR_PATTERN = re.compile(r'[\u0B80-\u0BFF]')

    CATEGORIES = {
        "QUANTITY_SHORTAGE": {
            "en": ["weigh", "weighed", "shortage", "less", "grams", "cheated", "cut", "reduced", "undervalue"],
            "ta": ["எடை", "குறைவு", "குறைவாக", "அளவு குறைவு", "மோசடி", "நிறுக்கவில்லை", "குறைத்து"]
        },
        "STOCK_UNAVAILABLE": {
            "en": ["stock", "unavailable", "empty", "no stock", "finished", "exhausted", "denied"],
            "ta": ["இருப்பு இல்லை", "சரக்கு இல்லை", "தீர்ந்துவிட்டது", "தரவில்லை", "இல்லை"]
        },
        "DELAYED_OPENING": {
            "en": ["closed", "delay", "late", "timing", "not opened", "waiting", "hours"],
            "ta": ["திறக்கவில்லை", "பூட்டியுள்ளது", "தாமதம்", "நேரம்", "காத்திருப்பு"]
        },
        "INCORRECT_ENTITLEMENT": {
            "en": ["quota", "entitlement", "card", "allotted", "ration card", "wrong", "rule"],
            "ta": ["ரேஷன் கார்டு", "அளவு", "உரிமம்", "கோட்டா", "கார்டில் இல்லை"]
        },
        "SHOP_BEHAVIOR": {
            "en": ["rude", "behavior", "shouted", "operator", "bribe", "harassment", "abusive"],
            "ta": ["நடத்தை", "திட்டினார்", "லஞ்சம்", "மரியாதை இல்லை", "தவறான பேச்சு"]
        },
        "QUALITY_ISSUE": {
            "en": ["quality", "spoiled", "worms", "stone", "smell", "bad", "dust", "insects"],
            "ta": ["தரம்", "புழு", "கல்", "நாற்றம்", "கெட்டுப்போன", "மோசமான அரிசி"]
        }
    }

    def detect_language(self, text: str) -> str:
        if self.TAMIL_CHAR_PATTERN.search(text):
            return "ta"
        return "en"

    def classify(self, text: str, user_lang: str = None) -> Dict[str, Any]:
        if not text or not text.strip():
            return {
                "detectedCategory": "OTHER",
                "confidenceScore": 0.50,
                "suggestedPriority": "LOW",
                "languageDetected": "en",
                "sentiment": "NEUTRAL",
                "extractedKeywords": [],
                "recommendedAction": "Request additional details from citizen."
            }

        lang = user_lang if user_lang in ["en", "ta"] else self.detect_language(text)
        text_lower = text.lower()

        scores = {}
        matched_keywords = []

        for category, lang_dict in self.CATEGORIES.items():
            score = 0
            # Check English keywords
            for kw in lang_dict.get("en", []):
                if kw in text_lower:
                    score += 2
                    matched_keywords.append(kw)
            # Check Tamil keywords
            for kw in lang_dict.get("ta", []):
                if kw in text_lower:
                    score += 3
                    matched_keywords.append(kw)
            scores[category] = score

        best_category = max(scores, key=scores.get)
        max_score = scores[best_category]

        if max_score > 0:
            confidence = min(0.95, 0.65 + (max_score * 0.08))
        else:
            best_category = "OTHER"
            confidence = 0.50

        # Assess priority
        if best_category in ["QUANTITY_SHORTAGE", "STOCK_UNAVAILABLE"]:
            priority = "HIGH"
            action = "Immediate taluk verification: Inspect shop stock scales and verify recent distribution indents."
        elif best_category == "QUALITY_ISSUE":
            priority = "HIGH"
            action = "Dispatch grain sampling inspector for lab moisture and cleanliness test."
        elif best_category in ["DELAYED_OPENING", "SHOP_BEHAVIOR"]:
            priority = "MEDIUM"
            action = "Issue formal notice to shop operator and inspect biometric login attendance records."
        else:
            priority = "LOW"
            action = "Standard helpline review: Contact cardholder for grievance clarification."

        sentiment = "NEGATIVE" if any(w in text_lower for w in ["fraud", "theft", "bribe", "மோசடி", "லஞ்சம்"]) else "NEUTRAL"

        return {
            "detectedCategory": best_category,
            "confidenceScore": round(confidence, 2),
            "suggestedPriority": priority,
            "languageDetected": lang,
            "sentiment": sentiment,
            "extractedKeywords": list(set(matched_keywords)),
            "recommendedAction": action
        }
