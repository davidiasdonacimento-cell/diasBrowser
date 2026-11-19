# diasBrowser

Navegador Android nativo desenvolvido em Kotlin, com interface Jetpack Compose e navegação real usando Android WebView.

## Identidade do projeto

- Nome: diasBrowser
- Aplicativo Android: com.diasbrowser.app
- Linguagem: Kotlin
- Interface: Jetpack Compose, tema Dark Premium
- Motor de páginas: Android WebView
- SDK mínimo: Android 8.0 (API 26)
- Build: Gradle e GitHub Actions

## Funcionalidades nesta primeira versão

- Abrir endereços HTTP e HTTPS e pesquisar termos no Google.
- Navegação voltar, avançar, atualizar e página inicial.
- Múltiplas abas, seleção e fechamento de abas.
- Favoritos persistidos localmente.
- Histórico local com opção de limpar.
- Downloads por meio do DownloadManager do Android.
- Abas temporárias que não gravam páginas no histórico local.
- Safe Browsing do WebView quando disponível.
- Interface escura com detalhes em azul, roxo e ciano.

## Privacidade: comportamento importante

As abas temporárias não registram navegações no histórico local e não usam cache HTTP. Nesta versão elas ainda compartilham o perfil de cookies e dados do WebView com as abas normais. Portanto, não devem ser consideradas um modo anônimo isolado. Uma implementação futura pode separar os perfis de navegação em processos Android diferentes.

## Gerar APK pelo GitHub Actions

1. Abra a aba Actions deste repositório.
2. Entre na execução mais recente chamada Android APK.
3. Aguarde os testes e a compilação terminarem com sucesso.
4. Na seção Artifacts, baixe diasBrowser-debug-apk.
5. Extraia o arquivo ZIP para obter app-debug.apk.

O workflow é iniciado automaticamente quando há push na branch main. Também pode ser iniciado manualmente em Actions.

## Compilar localmente

Requisitos: JDK 17, Android SDK com plataforma 36 e Gradle 8.13.

Na raiz do projeto:

    gradle testDebugUnitTest
    gradle assembleDebug

O APK de depuração será criado em:

    app/build/outputs/apk/debug/app-debug.apk

## Estrutura

    app/src/main/java/com/diasbrowser/app/
      MainActivity.kt
      BrowserApp.kt
      BrowserController.kt
      BrowserStore.kt
      UrlResolver.kt

    app/src/test/java/com/diasbrowser/app/
      UrlResolverTest.kt

    .github/workflows/android.yml

## Estado

Versão inicial do projeto. A compilação e os testes automatizados devem ser confirmados pela execução do GitHub Actions antes de declarar o APK validado.
