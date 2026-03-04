import { useMemo } from 'react'
import DataGrid from '../common/DataGrid'

export default function SpecialSubscriptionList({ items, selectedId, onRowClick }) {
  const columns = useMemo(() => [
    { accessorKey: 'subsBillStdId', header: '가입별과금기준ID', size: 160, minSize: 80 },
    { accessorKey: 'effStaDt',      header: '유효시작일',       size: 100, minSize: 60 },
    { accessorKey: 'subsId',        header: '가입ID',           size: 140, minSize: 80 },
    { accessorKey: 'svcCd',         header: '서비스코드',       size: 100, minSize: 60 },
    { accessorKey: 'statCd',        header: '상태코드',         size: 100, minSize: 60 },
    { accessorKey: 'lastEffYn',     header: '최종유효여부',     size: 100, minSize: 60 },
  ], [])

  // 복합 PK를 결합한 고유 식별자로 DataGrid에 데이터 전달
  const dataWithRowId = useMemo(
    () => items.map(item => ({ ...item, _rowId: `${item.subsBillStdId}__${item.effStaDt}` })),
    [items]
  )

  return (
    <DataGrid
      columns={columns}
      data={dataWithRowId}
      onRowClick={onRowClick}
      selectedRowId={selectedId}
      rowIdAccessor="_rowId"
    />
  )
}
