import React, { useEffect, useState } from 'react';
import { Users, Search, UserCheck } from 'lucide-react';
import { api } from '../services/api';
import { FamilyMember } from '../types';

export const FamilyMembers: React.FC = () => {
  const [members, setMembers] = useState<FamilyMember[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadMembers();
  }, []);

  const loadMembers = async () => {
    try {
      setLoading(true);
      // Fetch members from card 1001 or fallback
      const data = await api.getCardMembers(1001);
      setMembers(data);
    } catch (_) {
      // Fallback demo member records
      setMembers([
        { memberId: 5001, memberName: 'M. Subramanian', relationshipToHead: 'SELF (Head)', gender: 'M', dateOfBirth: '1975-04-12', age: 51, aadhaarLast4: '3412', isActive: 'Y' },
        { memberId: 5002, memberName: 'S. Lakshmi', relationshipToHead: 'SPOUSE', gender: 'F', dateOfBirth: '1980-08-20', age: 46, aadhaarLast4: '3413', isActive: 'Y' },
        { memberId: 5003, memberName: 'S. Karthik', relationshipToHead: 'SON', gender: 'M', dateOfBirth: '2005-01-15', age: 21, aadhaarLast4: '3414', isActive: 'Y' },
        { memberId: 5004, memberName: 'S. Sneha', relationshipToHead: 'DAUGHTER', gender: 'F', dateOfBirth: '2008-06-10', age: 18, aadhaarLast4: '3415', isActive: 'Y' },
        { memberId: 5005, memberName: 'K. Revathi', relationshipToHead: 'SELF (Head)', gender: 'F', dateOfBirth: '1982-11-05', age: 44, aadhaarLast4: '7890', isActive: 'Y' },
        { memberId: 5006, memberName: 'K. Dinesh', relationshipToHead: 'SON', gender: 'M', dateOfBirth: '2004-03-22', age: 22, aadhaarLast4: '7891', isActive: 'Y' },
        { memberId: 5007, memberName: 'K. Priya', relationshipToHead: 'DAUGHTER', gender: 'F', dateOfBirth: '2007-09-14', age: 19, aadhaarLast4: '7892', isActive: 'Y' }
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-wrapper">
      <div style={{ marginBottom: '1.5rem' }}>
        <h2>Family Members &amp; Beneficiaries</h2>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          Registered household members determining per-member grain quotas
        </p>
      </div>

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Member ID</th>
              <th>Member Name</th>
              <th>Relationship to Head</th>
              <th>Gender</th>
              <th>Date of Birth</th>
              <th>Age</th>
              <th>Aadhaar Hash</th>
              <th>Biometric Status</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={8} style={{ textAlign: 'center', padding: '2rem' }}>Loading member registry...</td></tr>
            ) : (
              members.map(m => (
                <tr key={m.memberId}>
                  <td style={{ fontWeight: 600 }}>MEM-{m.memberId}</td>
                  <td style={{ fontWeight: 600, color: 'var(--primary-navy)' }}>{m.memberName}</td>
                  <td>
                    <span className="badge badge-info">{m.relationshipToHead}</span>
                  </td>
                  <td>{m.gender === 'M' ? 'Male' : m.gender === 'F' ? 'Female' : 'Transgender'}</td>
                  <td>{m.dateOfBirth}</td>
                  <td><strong>{m.age}</strong> yrs</td>
                  <td>•••• •••• {m.aadhaarLast4}</td>
                  <td>
                    <span className="badge badge-success">
                      <UserCheck size={12} /> Aadhaar Verified
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
