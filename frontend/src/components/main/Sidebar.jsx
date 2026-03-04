import { NavLink, useLocation } from 'react-router-dom'

const MENU = [
  { label: 'Main', to: '/main' },
  {
    group: '가입관리',
    items: [
      { label: '가입관리',     to: '/subscriptions' },
      { label: '과금기준',     to: '/bill-std' },
      { label: '대표가입 관리', to: '/subscription-main' },
      { label: '특수가입관리', to: null },
    ],
  },
  {
    group: '시스템 설정',
    items: [
      { label: '사용자관리',   to: '/users' },
      { label: '공통코드관리', to: '/code' },
    ],
  },
  {
    group: '게시판',
    items: [
      { label: 'Q&A', to: '/qna' },
    ],
  },
]

export default function Sidebar() {
  const location = useLocation()

  const renderNavLink = (label, to) =>
    to ? (
      <NavLink
        key={label}
        to={to}
        className={() => {
          const isActive = location.pathname === to
          return `block px-4 py-2 text-sm transition-colors ${
            isActive
              ? 'text-[#2563EB] bg-blue-50 font-medium'
              : 'text-gray-600 hover:bg-gray-50 hover:text-gray-800'
          }`
        }}
      >
        {label}
      </NavLink>
    ) : (
      <span
        key={label}
        className="block px-4 py-2 text-sm text-gray-300 cursor-not-allowed"
      >
        {label}
      </span>
    )

  return (
    <aside className="w-56 bg-white border-r border-gray-200 flex-shrink-0 overflow-y-auto">
      <nav className="py-4">
        {/* 최상단 단독 항목 */}
        {MENU.filter(m => m.to !== undefined && m.label).map(({ label, to }) => (
          <div key={label} className="mb-2">
            {renderNavLink(label, to)}
          </div>
        ))}
        {/* 그룹 항목 */}
        {MENU.filter(m => m.group).map(({ group, items }) => (
          <div key={group} className="mb-4">
            <p className="px-4 py-1 text-sm font-semibold text-gray-500 uppercase tracking-wider">
              {group}
            </p>
            {items.map(({ label, to }) => renderNavLink(label, to))}
          </div>
        ))}
      </nav>
    </aside>
  )
}
