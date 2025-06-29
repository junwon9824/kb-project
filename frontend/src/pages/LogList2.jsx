import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { logApi } from '../apis/logApi';
import './LogList2.css';

const LogList2 = () => {
  const { accountNumber } = useParams();
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchLogs = async () => {
      try {
        const response = await logApi.getLogsByAccountNumber(accountNumber);
        setLogs(response.data ? response.data : response);
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
    <div className="log-container">
      <h1>계좌내역</h1>
      <table className="log-table">
        <thead>
          <tr>
            <th>날짜</th>
            <th>보내는사람 이름</th>
            <th>받는사람 이름</th>
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
              <td>{log.category}</td>
              <td>{log.amount || ''}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <a href="/users/main">메인으로 돌아가기</a>
    </div>
  );
};

export default LogList2; 