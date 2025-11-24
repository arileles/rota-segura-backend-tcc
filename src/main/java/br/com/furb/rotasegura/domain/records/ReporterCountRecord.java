package br.com.furb.rotasegura.domain.records;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.Set;

import br.com.furb.rotasegura.domain.records.UserRecord;

// Record now holds the full UserRecord and the occurrences count
public record ReporterCountRecord(UserRecordCount user, long occurrencesCount) { }
