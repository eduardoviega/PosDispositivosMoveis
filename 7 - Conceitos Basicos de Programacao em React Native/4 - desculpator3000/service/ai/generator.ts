import OpenAI from "openai";

const gemini = new OpenAI({
    apiKey: process.env.EXPO_PUBLIC_GEMINI_API_KEY,
    baseURL: "https://generativelanguage.googleapis.com/v1beta/openai/",
    dangerouslyAllowBrowser: true,
});

export async function geradorDesculpa(evento: string) {
    try {
        const cliente = gemini;
        const modelo = "gemini-3.1-flash-lite";

        const result = await cliente.chat.completions.create({
            model: modelo,
            messages: [
                {
                    role: "system",
                    content: "Gere apenas uma desculpa engraçada, criativa e que não ofenda ninguém",
                },
                { role: "user", content: evento },
            ],
            max_tokens: 100,
        });

        const texto = result.choices[0]?.message?.content;

        return texto || "Tenho de levar minha vó ao jiu-jitsu.";
    } catch (e) {
        console.error("ERRO:", e);
        return "Tenho de levar minha vó ao jiu-jitsu.";
    }
}
