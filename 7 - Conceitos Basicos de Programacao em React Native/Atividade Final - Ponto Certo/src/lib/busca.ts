/**
 * Busca simples para as listagens (projetos, materiais): sem acento e sem
 * diferenciar maiúscula/minúscula, para "croche" achar "Crochê".
 */

function normalizar(texto: string): string {
  return Array.from(texto.normalize('NFD'))
    .filter((caractere) => {
      const codigo = caractere.codePointAt(0) ?? 0;
      // Faixa dos acentos combinantes do Unicode (NFD separa a letra do acento).
      return codigo < 0x0300 || codigo > 0x036f;
    })
    .join('')
    .toLowerCase()
    .trim();
}

export function combina(consulta: string, ...campos: (string | undefined)[]): boolean {
  const alvo = normalizar(consulta);
  if (!alvo) return true;
  return campos.some((campo) => campo && normalizar(campo).includes(alvo));
}
