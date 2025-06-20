import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import './LogList.css';

const LogList = () => {
  const { accountNumber } = useParams();
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchLogs = async () => {
      try {
        const response = await axios.get(`/log/${accountNumber}`);
        setLogs(response.data);
      } catch (error) {
        console.error('로그 조회 실패:', error);
      } finally {
        setLoading(false);
      }
    };

    if (accountNumber) {
      fetchLogs();
    }
  }, [accountNumber]);

  if (loading) {
    return <div>로딩 중...</div>;
  }

  return (
    <div className="container">
      <div className="p-2 text-center">
        <h2>계좌내역</h2>
      </div>
      <div className="table-container">
        <table className="log-table">
          <thead>
            <tr>
              <th>날짜</th>
              <th>보내는사람</th>
              <th>받는사람</th>
              <th>카테고리</th>
              <th>금액</th>
            </tr>
          </thead>
          <tbody>
            {logs.map((log, index) => (
              <tr key={index}>
                <td>{log.createdDate}</td>
                <td>{log.sender_name}</td>
                <td>{log.recipient_name}</td>
                <td style={{ color: log.category === '송금' ? 'red' : 'blue' }}>
                  {log.category}
                </td>
                <td>{log.amount || ''}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default LogList; 