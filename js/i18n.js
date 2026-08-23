const supportedLanguages = ["en", "fr", "de"];

const script = document.currentScript;
const rel = script.dataset.rel || "";

console.log(rel);

async function loadTranslations(language) {
    if (!supportedLanguages.includes(language)) {
        language = "en";
    }

    const response = await fetch(`${rel}i18n/${language}.json`);

    if (!response.ok) {
        throw new Error(`Could not load ${language}.json`);
    }

    return await response.json();
}

async function setLanguage(language) {
    console.log(`Using language: ${language}`);
    const translations = await loadTranslations(language);

    document.querySelectorAll("[data-i18n]").forEach(element => {
        const key = element.dataset.i18n;

        if (translations[key] !== undefined) {
            element.textContent = translations[key];
        }
    });

    // Merge <title> contents into a single text string
    document.querySelectorAll("title").forEach(title => {
        let html = title.textContent;

        html = html.replace(
            /<span\b([^>]*)\bdata-i18n\s*=\s*["']([^"']+)["']([^>]*)>(.*?)<\/span>/gi,
            (_, before, key, after, content) => {
                return translations[key] !== undefined
                    ? translations[key]
                    : content;
            }
        );

        // Remove remaining span tags
        html = html.replace(/<\/?span\b[^>]*>/gi, "");

        // Normalize whitespace
        html = html.trim().replace(/\s+/g, " ");

        title.textContent = html;
    });

    localStorage.setItem("language", language);

    document.documentElement.lang = language;
}

const savedLanguage = localStorage.getItem("language");

const browserLanguage = navigator.language
    .split("-")[0];

const language =
    (supportedLanguages.includes((savedLanguage || browserLanguage))
        ? (savedLanguage || browserLanguage)
        : "en");

setLanguage(language);