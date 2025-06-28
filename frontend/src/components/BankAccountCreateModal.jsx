import React, { useState } from 'react';
import { bankAccountApi } from '../apis/bankAccountApi';
import './BankAccountCreateModal.css';

const BankAccountCreateModal = ({ isOpen, onClose, onSuccess, userid }) => {
    const [formData, setFormData] = useState({
        accountNumber: '',
        amount: '',
        bankname: '',
        password: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const banks = [
        { id: 1, name: '국민은행' },
        { id: 2, name: '신한은행' },
        { id: 3, name: '우리은행' },
        { id: 4, name: '하나은행' },
        { id: 5, name: '농협은행' }
    ];

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const bankAccountData = {
                accountNumber: formData.accountNumber,
                amount: parseInt(formData.amount),
                mainAccount: false,
                password: formData.password,
                bank: {
                    bankname: formData.bankname
                }
            };

            await bankAccountApi.createBankAccount(bankAccountData, userid);
            
            // 성공 시 폼 초기화
            setFormData({
                accountNumber: '',
                amount: '',
                bankname: '',
                password: ''
            });
            
            onSuccess(); // 부모 컴포넌트에 성공 알림
            onClose(); // 모달 닫기
        } catch (error) {
            console.error('계좌 생성 실패:', error);
            
            // 서버에서 전달된 에러 메시지 확인
            let errorMessage = '계좌 생성에 실패했습니다. 다시 시도해주세요.';
            
            if (error.response && error.response.data) {
                // 서버에서 전달된 에러 메시지가 있는 경우
                if (error.response.data.message) {
                    errorMessage = error.response.data.message;
                } else if (typeof error.response.data === 'string') {
                    errorMessage = error.response.data;
                }
            } else if (error.message) {
                // JavaScript 에러 메시지
                if (error.message.includes('동일한 계좌번호')) {
                    errorMessage = error.message;
                } else if (error.message.includes('404')) {
                    errorMessage = '서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.';
                }
            }
            
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <div className="modal-header">
                    <h2>새 계좌 생성</h2>
                    <button className="close-button" onClick={onClose}>&times;</button>
                </div>
                
                <form onSubmit={handleSubmit} className="modal-form">
                    <div className="form-group">
                        <label htmlFor="bankname">은행 선택</label>
                        <select
                            id="bankname"
                            name="bankname"
                            value={formData.bankname}
                            onChange={handleInputChange}
                            required
                        >
                            <option value="">은행을 선택하세요</option>
                            {banks.map(bank => (
                                <option key={bank.id} value={bank.name}>
                                    {bank.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label htmlFor="accountNumber">계좌번호</label>
                        <input
                            type="text"
                            id="accountNumber"
                            name="accountNumber"
                            value={formData.accountNumber}
                            onChange={handleInputChange}
                            placeholder="계좌번호를 입력하세요"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="amount">초기 잔액</label>
                        <input
                            type="number"
                            id="amount"
                            name="amount"
                            value={formData.amount}
                            onChange={handleInputChange}
                            placeholder="초기 잔액을 입력하세요"
                            min="0"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">계좌 비밀번호</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            value={formData.password}
                            onChange={handleInputChange}
                            placeholder="계좌 비밀번호를 입력하세요"
                            required
                        />
                    </div>

                    {error && <div className="error-message">{error}</div>}

                    <div className="modal-actions">
                        <button
                            type="button"
                            className="cancel-button"
                            onClick={onClose}
                            disabled={loading}
                        >
                            취소
                        </button>
                        <button
                            type="submit"
                            className="submit-button"
                            disabled={loading}
                        >
                            {loading ? '생성 중...' : '계좌 생성'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default BankAccountCreateModal; 