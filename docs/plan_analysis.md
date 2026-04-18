# 📊 Análise: Estratégia de Planos TableSplit

## Contexto Atual

O `Plan.java` atual já tem dois planos (`LITE` e `PRO`) com controle de **acesso a módulos**, mas **sem limites quantitativos** por recurso. Evoluir para um modelo com limites é a direção certa.

---

## ✅ Avaliação da Estratégia de Planos

A lógica dos 3 planos está bem pensada. Cada plano tem uma identidade clara:

| Aspecto | 🟢 Plano 1 | 🔵 Plano 2 | 🟣 Plano 3 |
|---|---|---|---|
| **Perfil do cliente** | Pequenos negócios, food trucks, lanchonetes | Restaurantes médios com mesas e equipe | Restaurantes grandes ou redes |
| **Custo de infra** | Baixo (só Cloudinary + BD leitura) | Médio (RabbitMQ, pedidos, equipe) | Alto e variável |
| **Pricing** | Fixo acessível | Fixo médio | Negociado |

Isso é um modelo SaaS saudável — **Good/Better/Best** com um tier custom no topo.

> Ver seção de nomes sugeridos para os planos ao final do documento.

---

## 🔢 Proposta de Limites por Recurso

### Raciocínio por recurso:

**Categorias** — impacto baixo, só BD. Limite generoso.  
**Itens do cardápio** — impacto médio, cada item tem imagem (Cloudinary).  
**Imagens/Galeria** — impacto direto em Cloudinary (storage + bandwidth). Limitação crítica.  
**Promoções** — impacto baixo, sem imagem obrigatória.  
**Mesas** — o principal **driver de valor e custo** do Plano 2 (mais mesas = mais pedidos simultâneos = mais WebSocket/RabbitMQ/BD).  
**Equipe (Staff)** — impacto indireto mas afeta concorrência de usuários.

### Limites sugeridos:

| Recurso | 🟢 Plano 1 | 🔵 Plano 2 | 🟣 Plano 3 |
|---|---|---|---|
| Categorias | 6 | 15 | Ilimitado |
| Itens do cardápio | 40 | 100 | Ilimitado |
| Imagens (Galeria) | 10 | 30 | Ilimitado |
| Promoções | 5 | 15 | Ilimitado |
| Mesas | ❌ (sem módulo) | 15 | Ilimitado |
| Equipe (Staff) | ❌ (sem módulo) | 5 | Ilimitado |
| **Retenção de Orders** | ❌ (sem módulo) | **90 dias** | **1 ano** |

> **Por que aumentei os limites do Plano 1?**  
> Um cardápio com apenas 30 itens pode frustrar restaurantes com cardápios variados (ex: pizzarias com massa + sabores + bebidas). 40 itens é mais realista para o segmento-alvo.

> **Por que 15 mesas no Plano 2?**  
> Restaurantes médios tipicamente têm entre 8 e 20 mesas. 15 é um número que atende a maioria sem empurrar para o Plano 3 cedo demais — o que seria frustrante para o cliente.

---

## 🏗️ Design Técnico Sugerido para `Plan.java`

A ideia de configurar os limites diretamente no enum é elegante e mantém tudo centralizado no domínio. Proposta de redesign:

```java
public enum Plan {
  MENU(
      PlanLimits.builder()
          .categories(6)
          .menuItems(40)
          .galleryImages(10)
          .promotions(5)
          .tables(0)        // módulo desabilitado
          .staff(0)         // módulo desabilitado
          .build(),
      Set.of(Module.DASHBOARD, Module.MENU, Module.GALLERY, Module.PROMOTIONS,
             Module.SETTINGS, Module.USER_PROFILE, Module.ACCOUNT)),

  PRO(
      PlanLimits.builder()
          .categories(15)
          .menuItems(100)
          .galleryImages(30)
          .promotions(15)
          .tables(15)
          .staff(5)
          .build(),
      Set.of(/* todos os módulos */)),

  ENTERPRISE(
      PlanLimits.UNLIMITED,
      Set.of(/* todos os módulos */));

  private final PlanLimits limits;
  private final Set<Module> modules;
}
```

```java
// PlanLimits.java — Value Object imutável
public record PlanLimits(
    int categories,
    int menuItems,
    int galleryImages,
    int promotions,
    int tables,
    int staff,
    int orderRetentionDays  // -1 = sem módulo de orders; 0 = indefinido (Enterprise custom)
) {
  public static final PlanLimits UNLIMITED =
      new PlanLimits(-1, -1, -1, -1, -1, -1, 0);

  public boolean isUnlimited(int value) {
    return value == -1;
  }
}
```

**Convenção:** `-1` = ilimitado. Simples de verificar em qualquer use case:

```java
// Exemplo de uso em CreateMenuItemUseCase
int limit = account.getPlan().getLimits().menuItems();
if (limit != -1 && currentCount >= limit) {
    throw new PlanLimitExceededException("Menu items limit reached for your plan.");
}
```

---

## 📌 Pontos que Você Pode Estar Esquecendo

| Aspecto | Consideração |
|---|---|
| **Pedidos** | Pedidos em si não precisam de limite numérico (é operacional), mas poderiam ter limite de **pedidos ativos simultâneos** no Plano 2 |
| **Reservas** | O módulo `RESERVATION` existe no `Module.java` — vale incluir no roadmap do Plano 2 ou 3 |
| **Relatórios** | O módulo `REPORTS` existe — Plano 2 poderia ter relatórios básicos; Plano 3, avançados |
| **Storage Cloudinary** | Além do número de imagens, considere limitar por **tamanho total em MB** no futuro |
| **Período de trial** | Ver seção detalhada abaixo |
| **Downgrade de plano** | Ver seção detalhada abaixo |
| **Renaming dos planos** | `LITE`/`PRO` já existe no código. Considere nomes mais descritivos para o usuário final: **Menu**, **Gestão**, **Enterprise** |

---

## ⏳ Período de Trial para o Plano 2

A fricção de conversão para o Plano 2 é alta porque o cliente precisa pagar antes de sentir o valor (mesas, pedidos, equipe). O trial resolve isso.

### Proposta: Trial de 14 dias do Plano 2

**Por que 14 dias?**
É tempo suficiente para o restaurante configurar tudo, treinar a equipe e operar pelo menos um final de semana completo — o momento onde o sistema prova seu valor sob pressão real.

### Modelo de ativação

O trial **não exige cartão de crédito** na ativação. Isso elimina a maior barreira de adoção. O pedido de pagamento só ocorre ao fim dos 14 dias.

### Design técnico no domínio

Adicionar campos ao `Account`:

```java
public class Account {
  // ... campos existentes ...
  private Plan plan;
  private boolean trialActive;
  private OffsetDateTime trialStartedAt;
  private OffsetDateTime trialEndsAt;

  public boolean isInTrial() {
    return trialActive && OffsetDateTime.now().isBefore(trialEndsAt);
  }

  public Plan getEffectivePlan() {
    // Se está em trial, age como PRO; senão, usa o plano real
    return isInTrial() ? Plan.PRO : plan;
  }
}
```

O `getEffectivePlan()` é a chave: **todos os use cases usam este método** em vez de `getPlan()` diretamente. Assim o trial é transparente para toda a aplicação.

### Fluxo completo

```
[Cadastro] → Plano MENU (padrão)
    ↓
[Usuário clica "Testar Plano Gestão grátis por 14 dias"]
    ↓
Account.trialActive = true
Account.trialStartedAt = now()
Account.trialEndsAt = now() + 14 dias
    ↓
[Durante 14 dias] → account.getEffectivePlan() == PRO → acesso total
    ↓
[Dia 12] → E-mail automático: "Seu trial acaba em 2 dias. Assine agora."
    ↓
[Dia 14 - expirado] → trialActive = false → volta ao MENU
    ↓
[Módulos de PRO ficam bloqueados, dados preservados]
```

### Regras importantes do trial

| Regra | Decisão |
|---|---|
| Quantos trials por conta? | **1 por lifetime** — evita abuso |
| Trial do Plano 3? | Não (é negociado, o trial é a própria conversa de venda) |
| Dados criados no trial somem? | **Não** — ficam bloqueados (read-only), não apagados |
| Trial após já ter sido PRO? | Não — só para contas que nunca foram PRO |

> [!TIP]
> Rastrear `trialUsed: boolean` no `Account` garante que um cliente que cancelou o PRO e voltou ao MENU não consiga outro trial — protege contra abuso sem ser punitivo.

---

## ⬇️ Política de Downgrade de Plano

Este é um dos cenários mais críticos do SaaS e precisa de uma política clara antes de implementar qualquer coisa, porque **downgrade sem política gera perda de dados ou experiência péssima**.

### O problema concreto

Um cliente no Plano 2 criou:
- 80 itens de cardápio (limite do Plano 1 = 40)
- 12 imagens na galeria (limite do Plano 1 = 10)
- 10 mesas (módulo inexistente no Plano 1)
- 3 staff (módulo inexistente no Plano 1)

**O que acontece se ele fizer downgrade para o Plano 1?**

### Estratégia recomendada: Bloqueio Suave (Soft Lock)

> [!IMPORTANT]
> **Não apague dados no downgrade.** Apagar dados cria desconfiança e potencialmente obrigações legais (LGPD). O correto é **bloquear** o excesso e **preservar** tudo.

**Como funciona:**

Após o downgrade, os dados existentes são **preservados mas bloqueados**:

| Recurso | Situação após downgrade |
|---|---|
| Itens além do limite | Existem, mas ficam **inativos automaticamente** — não aparecem no cardápio público |
| Imagens além do limite | **Read-only** — o cliente vê mas não pode editar nem adicionar novas |
| Mesas | **Módulo bloqueado** — a feature some da UI, dados ficam no BD |
| Staff | **Módulo bloqueado** — usuários extras perdem acesso ao dashboard |

O cliente recupera tudo ao fazer upgrade novamente.

### Design técnico

Adicionar um `AccountStatus` que represente o estado pós-downgrade:

```java
public enum AccountStatus {
  ACTIVE,        // plano em dia, tudo funcionando
  TRIAL,         // trial ativo
  LOCKED,        // downgrade — acima dos limites, funcionalidades bloqueadas
  SUSPENDED,     // inadimplência
  CANCELLED      // encerrado
}
```

O use case de downgrade executa:

```java
public void downgradePlan(Account account, Plan newPlan) {
  account.setPlan(newPlan);
  PlanLimits newLimits = newPlan.getLimits();

  // Desativa itens excedentes
  if (menuItemCount > newLimits.menuItems()) {
    menuItemRepository.deactivateExcess(account.getId(), newLimits.menuItems());
  }

  // Revoga acesso de staff excedente
  if (staffCount > newLimits.staff()) {
    staffRepository.revokeExcess(account.getId(), newLimits.staff());
  }

  // Muda status para LOCKED se algum recurso ficou acima do limite
  if (hasExcessResources(account, newPlan)) {
    account.setStatus(AccountStatus.LOCKED);
  }

  notificationService.sendDowngradeNotification(account);
}
```

### Comunicação com o cliente

**Antes do downgrade (confirmação):**
> "Você tem 80 itens de cardápio. O Plano Menu permite 40. Os 40 itens mais antigos serão desativados automaticamente. Você pode reativá-los ao fazer upgrade."

**E-mail pós-downgrade:**
> Lista exata dos itens afetados + CTA de upgrade.

### Qual dado bloquear primeiro?

| Opção | Prós | Contras |
|---|---|---|
| Mais antigos primeiro | Previsível, sem surpresas | Pode desativar itens populares |
| Menos pedidos nos últimos 30d | Inteligente, menos impacto | Mais complexo de implementar |
| **Deixar o cliente escolher** | Melhor UX, sem atrito | Requer UI de seleção antes do downgrade |

**Recomendação:** deixar o cliente escolher, dentro de uma tela de revisão antes de confirmar o downgrade. Isso transforma uma experiência potencialmente frustrante em uma oportunidade de engajamento.

---

## 🗑️ Retenção e Limpeza Automática de Orders/Tickets

Orders são o recurso que mais cresce no banco de dados ao longo do tempo — cada pedido gera múltiplos registros (order + items + status history). Sem política de limpeza, a tabela cresce indefinidamente mesmo para contas que mal usam o sistema.

### Por que é um diferenciador de plano?

Histórico = valor percebido. Quanto mais histórico o cliente pode acessar, mais ele depende do sistema para relatórios e análises. Isso cria **lock-in natural** e justifica o upgrade.

### Quanto tempo de retenção por plano?

| Plano | Retenção | Justificativa |
|---|---|---|
| 🟢 Plano 1 | N/A | Sem módulo de Orders |
| 🔵 Plano 2 | **90 dias** | Cobre 1 trimestre — suficiente para análise operacional e resolução de disputas recentes |
| 🟣 Plano 3 | **1 ano** | Cobre ciclos sazonais completos (ex: comparar Carnaval deste ano com o anterior) |

> [!NOTE]
> **90 dias foi escolhido para o Plano 2** porque: (1) cobre análise trimestral, (2) é o período em que disputas de pagamento normalmente ainda são abertas, e (3) é suficiente para o restaurante identificar tendências de consumo. Menos que isso (30d) seria restritivo; mais (180d) não justificaria o custo de infra do tier.

> [!TIP]
> **1 ano no Plano 3** permite comparação interanual ("vendemos mais esse Natal do que o anterior?"), que é exatamente o tipo de insight que convence um restaurante maior a pagar mais.

### Design técnico: Job de limpeza

Um Spring `@Scheduled` job que roda diariamente em horário de baixo tráfego:

```java
@Component
public class OrderRetentionCleanupJob {

  private final AccountRepository accountRepository;
  private final OrderRepository orderRepository;

  // Roda todo dia às 03:00 — horário de menor tráfego para restaurantes
  @Scheduled(cron = "0 0 3 * * *")
  public void execute() {
    List<Account> accounts = accountRepository.findAllActive();

    for (Account account : accounts) {
      int retentionDays = account.getPlan().getLimits().orderRetentionDays();

      // -1 = sem módulo de orders (Plano 1) → pula
      if (retentionDays == -1) continue;

      // 0 = Enterprise custom → não limpa automaticamente
      if (retentionDays == 0) continue;

      OffsetDateTime cutoff = OffsetDateTime.now().minusDays(retentionDays);
      int deleted = orderRepository.deleteClosedOrdersBefore(account.getId(), cutoff);

      log.info("[RetentionJob] Account={} deleted={} orders older than {} days",
          account.getId(), deleted, retentionDays);
    }
  }
}
```

```java
// OrderRepository
@Modifying
@Query("""
    DELETE FROM orders
    WHERE account_id = :accountId
      AND status IN ('CLOSED', 'CANCELLED')
      AND closed_at < :cutoff
    """)
int deleteClosedOrdersBefore(UUID accountId, OffsetDateTime cutoff);
```

### Regras importantes da limpeza

| Regra | Decisão |
|---|---|
| Apagar orders **abertas/ativas**? | **Nunca** — só `CLOSED` e `CANCELLED` |
| Apagar em batch único? | **Não** — usar `LIMIT` por execução para evitar lock na tabela |
| Log de auditoria? | **Sim** — registrar quantos foram deletados por conta/dia |
| Notificar o cliente? | **Não** — deve ser transparente; mencionar na página de planos |
| Plano 3 com retenção custom? | Configurar `orderRetentionDays` no BD, não hardcode |

### Limpeza em batch segura

Para evitar locks longos em tabelas grandes, deletar em lotes:

```java
// Deletar no máximo 500 por execução para não travar o BD
@Modifying
@Query(value = """
    DELETE FROM orders
    WHERE id IN (
      SELECT id FROM orders
      WHERE account_id = :accountId
        AND status IN ('CLOSED', 'CANCELLED')
        AND closed_at < :cutoff
      LIMIT 500
    )
    """, nativeQuery = true)
int deleteClosedOrdersBatchBefore(UUID accountId, OffsetDateTime cutoff);
```

---

## 🏷️ Sugestão de Nomes para os Planos (em inglês)

O nome do plano precisa comunicar identidade, não apenas funcionalidade. Três opções:

### Opção A — Temática de restaurante (recomendada)

| Código Java | Nome de produto | Sensação |
|---|---|---|
| `SHOWCASE` | **Showcase** | "Você está exibindo seu cardápio para o mundo" |
| `OPERATOR` | **Operator** | "Você está gerindo um restaurante de verdade" |
| `ENTERPRISE` | **Enterprise** | Padrão do mercado, clara para contas corporativas |

> Esta é a opção recomendada. **Showcase → Operator → Enterprise** conta uma história de crescimento: do restaurante que quer ser visto, para o que opera com eficiência, para o que escala.

### Opção B — Linguagem SaaS clássica

| Código Java | Nome de produto |
|---|---|
| `STARTER` | **Starter** |
| `PROFESSIONAL` | **Professional** |
| `ENTERPRISE` | **Enterprise** |

Simples, familiar. Boa se o público-alvo for menos tech-savvy e preferir clareza absoluta.

### Opção C — Voltada ao valor entregue

| Código Java | Nome de produto | Foco |
|---|---|---|
| `MENU` | **Menu** | O produto é o seu cardápio digital |
| `TABLE` | **Table** | O produto é a gestão do seu salão |
| `HOUSE` | **House** | O produto é o seu restaurante como um todo |

Elegante e temática. **Menu → Table → House** é memorável e explica o produto pelo próprio nome.

### Comparação final

| Critério | Opção A (Showcase/Operator) | Opção B (Starter/Pro) | Opção C (Menu/Table/House) |
|---|---|---|---|
| Memorabilidade | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| Clareza de valor | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| Identidade de produto | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| Familiaridade do mercado | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |

---

## 💶 Infraestrutura, Custos e Pricing para Portugal

### 1. Orçamento de Infraestrutura Railway (fase inicial)

Railway cobra por CPU + RAM consumidos por cada serviço. Para a fase 1 (1 réplica + PostgreSQL + RabbitMQ):

#### Estimativa de recursos por serviço

| Serviço | RAM estimada | CPU estimada | Custo/mês (est.) |
|---|---|---|---|
| **webapp** (Spring Boot) | 512 MB | 0.25 vCPU | ~$7–9 |
| **PostgreSQL** | 256 MB | 0.1 vCPU | ~$3–5 |
| **RabbitMQ** | 256 MB | 0.1 vCPU | ~$3–4 |
| **Railway Pro plan** | — | — | $20 (base) |
| **Total estimado** | | | **~$33–38/mês** |

> [!NOTE]
> O plano **Pro** do Railway ($20/mês) inclui $20 de créditos de uso. O excedente é cobrado à parte. Com os serviços acima, o custo total real ficará entre **$33–38/mês** (~€30–35/mês ao câmbio atual).

#### Fase 2 — Com Redis + réplica extra

| Serviço adicional | RAM | Custo extra/mês |
|---|---|---|
| **Redis** | 256 MB | ~$3–5 |
| **2ª réplica webapp** | 512 MB | ~$7–9 |
| **Extra total** | | ~$10–14 |
| **Total fase 2** | | **~$43–52/mês** (~€40–48/mês) |

---

### 2. Cloudinary — Custo extra a considerar

O free tier dá **25 créditos/mês** (1 crédito = 1 GB storage ou 1 GB bandwidth).

| Scenario | Créditos usados | Dentro do free? |
|---|---|---|
| 5 restaurantes Showcase (10 imgs cada) | ~0.5 GB storage + baixo bandwidth | ✅ Sim |
| 20 restaurantes mistos | ~5 GB storage + 5 GB bandwidth | ✅ Sim |
| 50+ restaurantes com Operator | >15 GB storage + bandwidth alto | ⚠️ Aproximando |

**Conclusão:** Cloudinary free aguenta bem até ~40–50 restaurantes ativos. A partir daí, o plano Plus (~$89/mês) entra em cena — mas só quando o negócio já tem receita para justificar.

---

### 3. Quantos restaurantes por réplica?

A maior pressão de recursos vem de **WebSockets ativos** (pedidos em tempo real, estado de mesas) e **queries de pedidos simultâneos**.

#### Modelo de carga por restaurante

| Plano | Conexões WebSocket | Queries/min (pico) |
|---|---|---|
| Showcase | 0 (sem orders) | ~5–10 (cardápio público) |
| Operator | 2–5 (staff + cozinha) | ~30–60 (pedidos ativos) |
| Enterprise | 5–15 | ~60–120 |

#### Capacidade estimada por réplica (512 MB / 0.25 vCPU)

| Mix de clientes | Restaurantes suportados |
|---|---|
| 100% Showcase | **80–120 restaurantes** |
| 70% Showcase + 30% Operator | **40–60 restaurantes** |
| 50% Showcase + 50% Operator | **25–35 restaurantes** |
| 100% Operator | **15–20 restaurantes** |

> [!TIP]
> **Recomendação prática:** Planeia escalar (adicionar réplica + Redis) quando atingires **30 restaurantes ativos no Operator** ou **60+ no Showcase**. Nesse ponto já terás receita suficiente para cobrir o custo extra de ~€12–15/mês.

---

### 4. Pricing para o Mercado Português

#### Contexto do mercado

| Concorrente | Tipo | Preço/mês |
|---|---|---|
| Menus digitais simples (QR code) | Apenas cardápio | €10–20/mês |
| Moloni ORDERS | Gestão de pedidos/POS | €15–30/mês (base + módulos) |
| Foodtic | Gestão sala + pedidos | ~€30–60/mês |
| WinRest 360 | Suite completa | €60–120+/mês |
| Cegid Vendus | POS completo | €40–80/mês |

O TableSplit posiciona-se entre o **menu digital simples** e o **POS completo** — um nicho real e pouco ocupado no mercado português.

#### Proposta de pricing

| Plano | Nome sugerido | Preço/mês (c/ IVA) | Preço anual | Posicionamento |
|---|---|---|---|---|
| 🟢 Plano 1 | **Showcase** | **€14,99/mês** | ~€120/ano (-33%) | Abaixo dos concorrentes de menu digital |
| 🔵 Plano 2 | **Operator** | **€39,99/mês** | ~€320/ano (-33%) | Entre Moloni e Foodtic |
| 🟣 Plano 3 | **Enterprise** | A partir de €79,99/mês | Negociado | Abaixo do WinRest/Cegid |

#### Análise de margem (fase inicial — 1 réplica)

Custo de infra: **~€32/mês**

| Cenário | Receita mensal | Margem |
|---|---|---|
| 5 Showcase + 3 Operator | €75 + €120 = **€195** | ~83% |
| 10 Showcase + 8 Operator | €150 + €320 = **€470** | ~93% |
| 20 Showcase + 15 Operator | €300 + €600 = **€900** | ~96% |

> [!IMPORTANT]
> Com apenas **5 clientes Operator**, já cobres toda a infraestrutura. O **break-even real** é atingido com 2–3 clientes Operator pagantes — o que é muito realista.

#### Justificativa do preço Showcase (€14,99)

- Concorrentes de menu digital simples: €10–20/mês
- Tu ofereces **dashboard, galeria, promoções e cardápio dinâmico** — valor superior
- €14,99 está abaixo do threshold psicológico de €15 e acima do que parece "demasiado barato"

#### Justificativa do preço Operator (€39,99)

- O Moloni ORDERS começa em ~€25/mês mas requer hardware POS adicional
- O Foodtic cobra ~€30–60/mês, mas é um sistema mais pesado
- €39,99 com mesas + pedidos + equipe + 90 dias de histórico é **competitivo e claro**

---

## 🚀 Estratégia de Escalamento — Do Piloto ao Mercado

### Fase 0 — Piloto privado (1 restaurante)

O objetivo desta fase **não é crescer, é validar**. Escolhe um restaurante de confiança — um familiar, amigo ou conhecido — que aceite ser o beta tester em troca de acesso gratuito ou com desconto significativo.

**O que validar nesta fase:**
- O fluxo completo de onboarding (cadastro → configuração do cardápio → primeira mesa)
- Estabilidade em ambiente real (horário de pico, conexões simultâneas, impressoras)
- UX real com staff que não tem paciência para UX ruim
- Bugs que só aparecem com dados reais e uso contínuo

**Critério de saída desta fase:** 2 semanas de operação estável sem bugs críticos, com feedback positivo do staff.

> [!IMPORTANT]
> Durante o piloto, **não cobres**. O restaurante está a fazer-te um favor enorme — eles são o teu QA team gratuito. Honra isso.

### Fase 1 — Beta fechado (3–5 restaurantes)

Expande para 3–5 restaurantes selecionados manualmente. Preço reduzido (~50%) em troca de feedback estruturado.

**O que fazer nesta fase:**
- Implementar o sistema de planos com limites (o código que estamos a desenhar neste documento)
- Monitorizar Railway (CPU, RAM, Railway metrics) com carga real
- Criar a primeira versão da página de pricing pública
- Recolher depoimentos e casos de uso reais para o marketing futuro

**Critério de saída:** Break-even de infra atingido (mínimo 3 clientes Operator pagantes).

### Fase 2 — Lançamento controlado (10–30 restaurantes)

Abertura pública com pricing definido, gateway de pagamento integrado, onboarding self-service. Foco total em aquisição.

### Fase 3 — Escala

Quando a infra começar a pressionar (>30 Operator ativos), acrescentar Redis + 2ª réplica conforme calculado na secção de infraestrutura.

---

## 📣 Estratégia de Divulgação e Vendas

### Por que representantes de vendas fazem sentido aqui

No mercado de restauração em Portugal, **a confiança é tudo**. Um dono de restaurante não vai assinar um software porque viu um anúncio no Instagram. Vai assinar porque **o dono do restaurante ao lado lhe disse que funciona**, ou porque um representante entrou pelo restaurante e fez uma demonstração ao vivo.

É exatamente o modelo que o WinRest e o Cegid usam — e é por isso que dominam o mercado B2B de restaurantes há anos.

### Os três canais recomendados

#### 1. 🤝 Representante de Vendas (canal mais eficaz)

Um representante visita restaurantes, faz demos ao vivo, tira dúvidas no local. É o canal mais eficaz para este mercado mas também o mais caro em tempo.

**Modelo de comissão recomendado:**

| Tipo | Estrutura | Valor estimado |
|---|---|---|
| **Recurring share** | 20% do MRR do cliente durante 12 meses | Showcase: €3/mês | Operator: €8/mês |
| **One-time bounty** | Flat fee por conversão | €40–80 por cliente Operator |
| **Híbrido (recomendado)** | €30 na conversão + 15% durante 6 meses | Melhor equilíbrio motivação/custo |

> [!TIP]
> Começa com o modelo **híbrido**. O pagamento na conversão motiva o rep imediatamente; a % recorrente incentiva-o a escolher bons clientes (que não vão cancelar ao 2º mês).

**A quem recrutar como rep:**
- Fornecedores de equipamento de cozinha (já têm relação de confiança com os donos)
- Técnicos de informática que servem restaurantes
- Ex-funcionários de concorrentes como WinRest ou Cegid

#### 2. 🌐 Programa de Indicação (referral)

Restaurantes conhecem outros restaurantes. O teu cliente mais satisfeito é o teu melhor vendedor.

**Proposta de incentivo:**

| Quem indica | Recompensa |
|---|---|
| **Restaurante que indica** | 1 mês grátis por cada indicação que converte |
| **Restaurante que foi indicado** | 1 mês grátis (além do trial normal) |

Isso cria um **double incentive** — o referido entra com 14 dias de trial + 1 mês grátis = quase 6 semanas antes de pagar. Reduz drasticamente o risco percebido.

**Implementação técnica simples:**
```
account.referralCode = "TASTE-A3K9"   // gerado no cadastro
account.referredBy = UUID               // quem indicou
```
Quando o referido converte, aplica o crédito automaticamente via Stripe Customer Balance.

#### 3. 📱 Marketing Digital (canal de suporte)

Não é o canal principal para este mercado, mas complementa os outros:

| Canal | O que fazer |
|---|---|
| **Instagram/Facebook** | Mostrar o produto em ação (video real num restaurante) |
| **Google Ads** | Capturar intenção: "software gestão restaurante portugal" |
| **SEO** | Blog com conteúdo útil para donos de restaurantes |

> [!NOTE]
> Não investir em marketing digital antes de ter os primeiros 10 clientes orgânicos e produto estável. O ROI é muito baixo sem prova social (depoimentos, casos de uso).

### Resumo da estratégia de aquisição por fase

| Fase | Canal principal | Meta |
|---|---|---|
| Piloto | Rede pessoal | 1 restaurante grátis |
| Beta | Rede pessoal + referral | 3–5 pagantes |
| Lançamento | Reps + referral + Instagram | 15–30 pagantes |
| Escala | Reps com comissão estruturada + SEO | 50+ pagantes |

---

## 💳 Gateway de Pagamento — Stripe

### Por que Stripe

Stripe é a escolha natural para um SaaS em Portugal por:
- Suporte completo a subscriptions com trial, upgrade, downgrade e cancellation automáticos
- Webhooks fiáveis para sincronizar o estado do plano com a tua aplicação
- Dashboard de billing profissional que poupas meses de desenvolvimento
- Suporte à EU (GDPR compliant, IVA automático via Stripe Tax)

### Métodos de pagamento disponíveis em Portugal

> [!WARNING]
> **MB WAY e Multibanco NÃO suportam recorrência.** São métodos push (o cliente inicia o pagamento). Não podes usá-los para cobranças automáticas mensais.

| Método | Suporta subscriptions? | Uso recomendado |
|---|---|---|
| **Cartão (Visa/MC)** | ✅ Sim | Método principal para subscriptions |
| **SEPA Direct Debit** | ✅ Sim | Alternativa para quem prefere débito bancário |
| **MB WAY** | ❌ Não | Apenas para pagamentos únicos (setup fee, etc.) |
| **Multibanco** | ❌ Não | Apenas para pagamentos únicos |

**Estratégia:** subscriptions automáticas via Cartão ou SEPA; oferecer MB WAY/Multibanco apenas para taxa de setup inicial ou add-ons opcionais.

### Mapeamento Stripe ↔ TableSplit

```
Stripe Product  → Plan (SHOWCASE, OPERATOR, ENTERPRISE)
Stripe Price    → Preço mensal ou anual do plano
Stripe Customer → Account
Stripe Subscription → Account.subscription_id
```

### Webhooks críticos a implementar

| Evento Stripe | Ação no TableSplit |
|---|---|
| `customer.subscription.trial_will_end` | Enviar e-mail "Faltam 2 dias para o trial acabar" |
| `invoice.payment_succeeded` | Marcar conta como `ACTIVE`, restaurar acesso |
| `invoice.payment_failed` | Marcar conta como `SUSPENDED`, banner in-app |
| `customer.subscription.updated` | Detectar upgrade/downgrade, ajustar plano e limites |
| `customer.subscription.deleted` | Marcar conta como `CANCELLED`, iniciar Soft Lock |

### Fluxo de subscrição automático

```
[Utilizador escolhe Operator] 
    ↓
[Stripe Checkout Session criada] 
    ↓
[Utilizador insere cartão]
    ↓
[Stripe cria Subscription com trial_end = now + 14d]
    ↓
[Webhook: subscription.created] → Account.status = TRIAL
    ↓
[Dia 12] → Stripe envia customer.subscription.trial_will_end
           → Teu backend envia e-mail de aviso
    ↓
[Dia 14] → Stripe cobra automaticamente
         → invoice.payment_succeeded → Account.status = ACTIVE
         OU
         → invoice.payment_failed → Account.status = SUSPENDED
```

### Consideração fiscal importante 🇵🇹

Stripe **não gera faturas conformes com a AT portuguesa**. Para compliance fiscal, precisas de integrar com um software de faturação certificado:

| Opção | Custo | Integração |
|---|---|---|
| **Moloni** | ~€15/mês | API REST bem documentada |
| **Invoicexpress** | ~€12/mês | REST API, muito usada por SaaS PT |
| **Sage** | ~€20/mês | Mais complexo, para volume maior |

**Recomendação:** Moloni ou Invoicexpress — ambos têm API, funcionam bem com Stripe via webhooks, e são aceites pela AT.

> [!CAUTION]
> Não adiar a questão fiscal. Em Portugal, **faturação não certificada pode resultar em coimas da AT**. Implementar desde o primeiro cliente pagante.

---

## 🎯 Conclusão

A estratégia está **sólida e bem fundamentada**. Os três pilares (acessibilidade, uso real de infra, personalização) são exatamente o que differencia bons modelos SaaS.

**Prioridades técnicas recomendadas:**
1. Criar `PlanLimits` com `orderRetentionDays` como value object/record imutável
2. Associar limites ao `Plan` enum com os novos nomes (`SHOWCASE`, `OPERATOR`, `ENTERPRISE`)
3. Adicionar `trialActive`, `trialEndsAt`, `trialUsed` e `AccountStatus` ao `Account`
4. Implementar verificação de limites nos use cases de criação (categoria, item, galeria, mesa, staff)
5. Implementar use case de downgrade com Soft Lock
6. Implementar `OrderRetentionCleanupJob` com deleção em batch
7. Integrar Stripe Billing com webhooks para automatizar trial, upgrade, downgrade e suspensão
8. Integrar Moloni ou Invoicexpress para emissão de faturas conformes com a AT
9. Expor os limites via API para o frontend mostrar progresso (ex: "32/40 itens usados")

> [!IMPORTANT]
> A exposição dos limites no frontend é estratégica — o usuário vendo `38/40 itens` naturalmente converte para o Plano 2 sem nenhum esforço de vendas.
