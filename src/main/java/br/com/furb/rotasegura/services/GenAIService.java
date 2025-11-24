package br.com.furb.rotasegura.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.gson.Gson;
import br.com.furb.rotasegura.domain.entities.Media;
import br.com.furb.rotasegura.domain.entities.Occurrence;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceSeverity;
import br.com.furb.rotasegura.domain.enumerators.OccurrenceType;
import br.com.furb.rotasegura.repositories.MediaRepository;
import br.com.furb.rotasegura.repositories.OccurenceRepository;
import jakarta.transaction.Transactional;

@Service
public class GenAIService {

    private static final String ANALYZE_MEDIA_PROMPT = """
        Instrução: Analise a imagem em anexo e retorne APENAS um objeto JSON.

            Contexto e Definições:

            Classificações de Desastre Válidas:

           - ENCHENTE (Elevação anormal do nível de rios ou cursos d’água, provocando transbordamento e inundação de áreas ao redor)
           - ALAGAMENTO_URBANO (Acúmulo de água nas ruas e áreas urbanas devido à chuva intensa, drenagem insuficiente ou entupimento de bueiros)
           - DESLIZAMENTO (Movimento de massa de terra, rochas ou lama em encostas, causado por chuvas fortes ou instabilidade do solo)
           - DESMORONAMENTO (Queda repentina de estruturas naturais ou artificiais (paredes, barrancos, rochas), geralmente em áreas urbanas ou obras)
           - DESABAMENTO_DE_EDIFICIO (Colapso total ou parcial de construções devido a falhas estruturais, explosões ou desastres secundários)
           - INCENDIO_FLORESTAL (Queimadas de grandes proporções que se espalham por áreas de vegetação, afetando fauna, flora e comunidades próximas)
           - TEMPESTADE (Fenômeno atmosférico com chuva intensa, ventos fortes e, possivelmente, raios ou trovoadas)
           - VENTOS_FORTES (Rajadas intensas de vento que podem causar destelhamentos, queda de árvores ou postes)
           - GRANIZO (Precipitação de pedras de gelo que danificam plantações, veículos e construções)
           - TORNADO (Coluna de ar giratória extremamente violenta conectando nuvem e solo, capaz de causar destruição severa)
           - ONDA_DE_CALOR (Período prolongado de temperaturas excepcionalmente altas, podendo causar problemas de saúde e secas)
           - OUTRO (Ocorrência que não se enquadra nas categorias conhecidas, mas apresenta características de desastre natural)
           - DESCONHECIDO (Caso em que não há informações suficientes para determinar o tipo de desastre)

            Níveis de Severidade Válidos:

            - ALTO
            - MEDIO
            - BAIXO

            Critérios de Severidade:

            - ALTO: Risco iminente à vida. Impede totalmente o tráfego de moradores e forças de emergência. Destruição significativa.
            - MEDIO: Risco presente. Forças de emergência conseguem trafegar (com dificuldade), mas moradores têm tráfego impossibilitado ou muito difícil.
            - BAIXO: Risco mínimo. Existe um inconveniente claro, mas não impede o tráfego de moradores ou forças de emergência.

        Formato de Saída Obrigatório (JSON): Seu retorno deve ser exclusivamente um objeto JSON, sem nenhum texto explicativo antes ou depois e sem nenhuma formatação adicional (markdown ou outra qualquer). A saída deve ser exatamente assim:

            {
                "classificacao": "VALOR_AQUI",
                "severidade": "VALOR_AQUI"
            }

            Regras Adicionais:

            - Se a imagem não representar claramente um dos desastres listados, ou se não for um desastre, use DESCONHECIDO para ambos os campos.
            - A "classificacao" deve ser exatamente uma das Classificações Válidas (ou DESCONHECIDO).
            - A "severidade" deve ser exatamente um dos Níveis de Severidade Válidos (ou DESCONHECIDO), com base nos Critérios de Severidade.
            """;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Autowired
    private OccurenceRepository occurrenceRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Transactional
    public boolean analyze(UUID occurrenceId) {

        Occurrence occurrence = occurrenceRepository.findById(occurrenceId).orElseThrow();
        List<Media> medias = mediaRepository.findByOccurrenceId(occurrence.getId());
        List<OccurrenceSeverity> severities = new ArrayList<>();
        List<OccurrenceType> types = new ArrayList<>();

        try (Client client = Client.builder().apiKey(apiKey).build()) {

            // Itera sobre cada mídia associada à ocorrência.
            for (Media media : medias) {
                try {

                    // Prepara a conteúdo da conversa com o chat.
                    Part image = Part.fromBytes(media.getImageData(), media.getMediaType());
                    Part prompt = Part.fromText(ANALYZE_MEDIA_PROMPT);
                    Content content = Content.builder().parts(image, prompt).build();

                    // Envia a solicitação para o modelo Gemini.
                    GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", content, null);

                    // Processa a resposta JSON.
                    AnalyzeResult result = getResponseResult(response);

                    // Atualiza a mídia com os resultados da análise.
                    media.setAiType(OccurrenceType.valueOf(result.classificacao));
                    media.setAiSeverity(OccurrenceSeverity.valueOf(result.severidade));
                    mediaRepository.save(media);

                } catch (Exception e) {
                    e.printStackTrace();
                    media.setAiSeverity(OccurrenceSeverity.DESCONHECIDO);
                    media.setAiType(OccurrenceType.DESCONHECIDO);
                    mediaRepository.save(media);
                }

                // Coleta os resultados para agregação posterior.
                severities.add(media.getAiSeverity());
                types.add(media.getAiType());
            }
        }

        // Agrega os resultados das mídias para atualizar a ocorrência.
        severities.stream()
            .sorted((a, b) -> b.ordinal() - a.ordinal())
            .findFirst()
            .ifPresent(occurrence::setAiSeverity);
        types.stream()
            .collect(Collectors.groupingBy(t -> t, Collectors.counting()))
            .entrySet().stream()
            .max(Entry.comparingByValue())
            .map(Entry::getKey)
            .ifPresent(occurrence::setAiType);
        occurrenceRepository.save(occurrence);

        return true;
    }

    private static final String JSON_PREFIX = "```json";
    private static final String JSON_SUFFIX = "```";

    private AnalyzeResult getResponseResult(GenerateContentResponse response) {

        String text = response.text();
        System.out.println(text);

        if (text.startsWith(JSON_PREFIX) && text.endsWith(JSON_SUFFIX)) {
            text = text.substring(JSON_PREFIX.length(), text.length() - JSON_SUFFIX.length()).trim();
        }

        return new Gson().fromJson(text, AnalyzeResult.class);
    }

    private record AnalyzeResult(String classificacao, String severidade) {

    }
}

