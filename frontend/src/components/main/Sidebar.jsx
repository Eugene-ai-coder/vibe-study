import { useState, useEffect, useCallback } from 'react'
import { NavLink, useLocation } from 'react-router-dom'

const MENU = [
  { label: 'Main', to: '/main' },
  {
    group: '가입관리',
    items: [
      { label: '가입관리',     to: '/subscriptions' },
      { label: '과금기준',     to: '/bill-std' },
      { label: '대표가입 관리', to: '/subscription-main' },
      { label: '특수가입관리', to: '/special-subscription' },
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

function findGroupForPath(pathname) {
  for (const entry of MENU) {
    if (entry.group && entry.items?.some(item => item.to === pathname)) {
      return entry.group
    }
  }
  return null
}

export default function Sidebar() {
  const location = useLocation()

  const [openGroups, setOpenGroups] = useState(() => {
    const active = findGroupForPath(location.pathname)
    return active ? new Set([active]) : new Set()
  })

  useEffect(() => {
    const active = findGroupForPath(location.pathname)
    if (active) {
      setOpenGroups(prev => {
        if (prev.has(active)) return prev
        return new Set([...prev, active])
      })
    }
  }, [location.pathname])

  const toggleGroup = useCallback((group) => {
    setOpenGroups(prev => {
      const next = new Set(prev)
      if (next.has(group)) next.delete(group)
      else next.add(group)
      return next
    })
  }, [])

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
        {/* 그룹 항목 (아코디언) */}
        {MENU.filter(m => m.group).map(({ group, items }) => {
          const isOpen = openGroups.has(group)
          return (
            <div key={group} className="mb-1">
              <button
                type="button"
                onClick={() => toggleGroup(group)}
                className="w-full flex items-center justify-between px-4 py-2 text-sm font-semibold text-gray-500 uppercase tracking-wider hover:bg-gray-50 transition-colors"
              >
                <span>{group}</span>
                <span className="text-xs text-gray-400">{isOpen ? '▾' : '▸'}</span>
              </button>
              {isOpen && items.map(({ label, to }) => renderNavLink(label, to))}
            </div>
          )
        })}
      </nav>
    </aside>
  )
}
