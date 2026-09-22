# FinTrack data layer

## Money representation

FinTrack stores money as a positive `Long` containing the currency's minor units. For example,
USD 12.50 is stored as `1250`. Transaction direction is represented separately by
`TransactionType.INCOME` or `TransactionType.EXPENSE`.

This avoids binary floating-point rounding errors and keeps aggregation in SQLite exact. The
selected currency and its fraction-digit rules will be handled at the formatting and input
boundaries when settings are introduced. Version 1 does not perform currency conversion.

## Time representation

Transaction timestamps are `Instant` values in the domain layer and epoch milliseconds in Room.
Period queries use a start-inclusive, end-exclusive interval: `[start, end)`. Month boundaries
must be calculated in the user's time zone outside the DAO and converted to `Instant` before the
query. This avoids overlapping records at the boundary between two months.

## Database lifecycle

`FinTrackDatabase` starts at schema version 1 and exports its schema to `app/schemas`. Every future
schema change must increment the database version, add a migration, and add a migration test. The
production database builder intentionally does not use destructive migration fallback.

## Dependency direction

The presentation layer depends on domain repository interfaces. The data layer implements those
interfaces and is the only layer that knows about Room entities and DAOs. Hilt binds
`OfflineTransactionRepository` to `TransactionRepository`, keeping ViewModels independent of the
storage implementation.
