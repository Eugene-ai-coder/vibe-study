import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { searchSubscriptions } from '../../api/subscriptionApi'
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer
} from 'recharts'

const STATUS_CD = { ACTIVE: '활성', SUSPENDED: '정지', TERMINATED: '탈퇴', PENDING: '미납' }

function getRecentMonths(data) {
  const counts = {}
  const now = new Date()
  for (let i = 5; i >= 0; i--) {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
    const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
    counts[key] = 0
  }
  data.forEach(item => {
    if (!item.subsDt) return
    const key = item.subsDt.slice(0, 7)
    if (key in counts) counts[key]++
  })
  return Object.entries(counts).map(([month, count]) => ({ month, count }))
}

export default function DashboardContent() {
  const [items, setItems] = useState([])
  const navigate = useNavigate()

  useEffect(() => {
    searchSubscriptions().then(setItems).catch(() => {})
  }, [])

  const total      = items.length
  const active     = items.filter(i => i.subsStatusCd === 'ACTIVE').length
  const suspended  = items.filter(i => i.subsStatusCd === 'SUSPENDED').length
  const terminated = items.filter(i => i.subsStatusCd === 'TERMINATED').length
  const pending    = items.filter(i => i.subsStatusCd === 'PENDING')
  const chartData  = getRecentMonths(items)

  const CARDS = [
    { label: '전체 가입자', value: total,     color: 'text-gray-800' },
    { label: '활성',        value: active,    color: 'text-green-600' },
    { label: '정지',        value: suspended, color: 'text-yellow-600' },
    { label: '탈퇴',        value: terminated,color: 'text-red-500' },
  ]

  return (
    <div className="space-y-6">
      <h1 className="text-xl font-bold text-gray-800">대시보드</h1>

      {/* 현황 카드 */}
      <div className="grid grid-cols-4 gap-4">
        {CARDS.map(({ label, value, color }) => (
          <div key={label} className="bg-white rounded-lg shadow-sm p-5">
            <p className="text-xs text-gray-400 mb-1">{label}</p>
            <p className={`text-3xl font-bold ${color}`}>{value}</p>
          </div>
        ))}
      </div>

      {/* 차트 */}
      <div className="bg-white rounded-lg shadow-sm p-5">
        <h2 className="text-sm font-semibold text-gray-700 mb-4">월별 신규 가입</h2>
        <ResponsiveContainer width="100%" height={200}>
          <BarChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} />
            <XAxis dataKey="month" tick={{ fontSize: 12 }} />
            <YAxis allowDecimals={false} tick={{ fontSize: 12 }} />
            <Tooltip />
            <Bar dataKey="count" name="신규 가입" fill="#2563EB" radius={[4, 4, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* 미납자 리스트 */}
      <div className="bg-white rounded-lg shadow-sm p-5">
        <h2 className="text-sm font-semibold text-gray-700 mb-3">
          미납 가입 <span className="text-[#2563EB] font-bold">{pending.length}</span>건
        </h2>
        {pending.length === 0 ? (
          <p className="text-sm text-gray-400">미납 가입이 없습니다.</p>
        ) : (
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-100">
                {['가입ID', '가입명', '서비스명'].map(h => (
                  <th key={h} className="py-1 text-left text-xs text-gray-500 font-medium">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {pending.map(item => (
                <tr
                  key={item.subsId}
                  className="border-b border-gray-50 hover:bg-blue-50 cursor-pointer"
                  onClick={() => navigate(`/subscriptions?subsId=${item.subsId}`)}
                >
                  <td className="py-1.5 text-[#2563EB]">{item.subsId}</td>
                  <td className="py-1.5">{item.subsNm}</td>
                  <td className="py-1.5">{item.svcNm}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}
