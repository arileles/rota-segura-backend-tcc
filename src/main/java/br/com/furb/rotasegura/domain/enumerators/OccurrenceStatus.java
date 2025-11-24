package br.com.furb.rotasegura.domain.enumerators;

public enum OccurrenceStatus {
    REPORTED,     // criado e aguardando tratamento
    IN_PROGRESS,  // em atendimento/mitigação
    RESOLVED,     // resolvido
    DISMISSED     // descartado/duplicado/indevido
}