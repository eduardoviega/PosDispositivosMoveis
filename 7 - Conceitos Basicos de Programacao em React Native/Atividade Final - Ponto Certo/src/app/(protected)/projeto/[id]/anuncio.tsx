import * as Clipboard from 'expo-clipboard';
import { useGlobalSearchParams, useRouter } from 'expo-router';
import { useState } from 'react';
import { View } from 'react-native';

import { Aviso, Botao, Cartao, Rotulo, Selecao, Tela, Texto } from '@/components/ui';
import { Anuncio } from '@/domain/types';
import { CANAIS, Canal, gerarAnuncio, Tom, TONS } from '@/service/ai/anuncio';
import { useDados } from '@/state/dados';
import { esp, raio, useCores } from '@/theme';

export default function TelaAnuncio() {
  const { id } = useGlobalSearchParams<{ id: string }>();
  const router = useRouter();
  const c = useCores();
  const { projetoPorId } = useDados();

  const projeto = projetoPorId(id);

  const [tom, setTom] = useState<Tom>('fofo');
  const [canal, setCanal] = useState<Canal>('Instagram');
  const [anuncio, setAnuncio] = useState<{ dados: Anuncio; aviso?: string } | null>(null);
  const [gerando, setGerando] = useState(false);
  const [copiado, setCopiado] = useState(false);

  if (!projeto) {
    return (
      <Tela>
        <Texto>Esta peça não existe mais.</Texto>
        <Botao titulo="Voltar" variante="secundario" aoTocar={() => router.back()} />
      </Tela>
    );
  }

  async function gerar() {
    setGerando(true);
    try {
      const r = await gerarAnuncio(projeto!, tom, canal);
      setAnuncio({ dados: r.dados, aviso: r.aviso });
    } finally {
      setGerando(false);
    }
  }

  async function copiar() {
    if (!anuncio) return;
    const texto = [
      anuncio.dados.titulo,
      '',
      anuncio.dados.descricao,
      '',
      ...anuncio.dados.bullets.map((b) => `• ${b}`),
      '',
      anuncio.dados.hashtags.join(' '),
    ].join('\n');
    await Clipboard.setStringAsync(texto);
    setCopiado(true);
    setTimeout(() => setCopiado(false), 2000);
  }

  return (
    <Tela>
      <Cartao>
        <Rotulo>Peça</Rotulo>
        <Texto variante="secao">{projeto.nome}</Texto>
        <Texto variante="pequeno" cor="tinta2">
          {projeto.consumos.map((x) => x.materialNome).join(', ') || 'sem material lançado'}
        </Texto>
      </Cartao>

      <Selecao rotulo="Tom" opcoes={TONS} valor={tom} aoMudar={setTom} />
      <Selecao rotulo="Canal" opcoes={CANAIS} valor={canal} aoMudar={setCanal} />

      <Botao
        titulo={anuncio ? 'Gerar de novo' : 'Escrever o anúncio'}
        aoTocar={gerar}
        carregando={gerando}
      />

      {anuncio ? (
        <Cartao>
          <Rotulo>Título</Rotulo>
          <Texto variante="secao">{anuncio.dados.titulo}</Texto>

          <Rotulo>Descrição</Rotulo>
          <View
            style={{
              backgroundColor: c.superficieAlt,
              borderRadius: raio.sm,
              padding: esp.md,
            }}>
            <Texto>{anuncio.dados.descricao}</Texto>
          </View>

          <Rotulo>Pontos</Rotulo>
          <View style={{ gap: esp.xs }}>
            {anuncio.dados.bullets.map((b) => (
              <Texto key={b} variante="pequeno" cor="tinta2">
                • {b}
              </Texto>
            ))}
          </View>

          <Rotulo>Hashtags</Rotulo>
          <Texto variante="pequeno" cor="acento">
            {anuncio.dados.hashtags.join('  ')}
          </Texto>

          {anuncio.aviso ? <Aviso tom="neutro">{anuncio.aviso}</Aviso> : null}

          <Botao
            titulo={copiado ? 'Copiado' : 'Copiar anúncio'}
            variante="secundario"
            aoTocar={copiar}
          />
          <Texto variante="pequeno" cor="tinta3">
            Texto sugerido: revise antes de publicar.
          </Texto>
        </Cartao>
      ) : null}
    </Tela>
  );
}
