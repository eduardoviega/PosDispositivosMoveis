import { useState } from 'react';
import { Alert, View } from 'react-native';

import {
  Aviso,
  Botao,
  Campo,
  Cartao,
  LinhaValor,
  Rotulo,
  Selecao,
  Tela,
  Texto,
} from '@/components/ui';
import {
  PASSOS_ARREDONDAMENTO,
  PassoArredondamento,
  PerfilPrecificacao,
} from '@/domain/types';
import { escreverCentavos, formatarReais, lerCentavos, lerNumero } from '@/lib/dinheiro';
import { excluirConta, sair } from '@/service/auth/cliente';
import { calcularPrecificacao, validarPerfil } from '@/service/pricing';
import { useDados } from '@/state/dados';
import { esp } from '@/theme';

function escreverPercentual(fracao: number): string {
  return String(Number((fracao * 100).toFixed(2))).replace('.', ',');
}

export default function TelaAjustes() {
  const { usuario, perfil, salvarPerfil, projetos, materiais, apagarTudo } = useDados();
  const [saindo, setSaindo] = useState(false);
  const [excluindo, setExcluindo] = useState(false);

  function confirmarSaida() {
    Alert.alert('Sair da conta', `Deseja sair de ${usuario.email ?? 'sua conta'}?`, [
      { text: 'Cancelar', style: 'cancel' },
      {
        text: 'Sair',
        style: 'destructive',
        onPress: async () => {
          setSaindo(true);
          try {
            await sair();
            // O observador no _layout raiz troca de tela sozinho ao detectar
            // a sessão encerrada.
          } finally {
            setSaindo(false);
          }
        },
      },
    ]);
  }

  function confirmarExclusaoDaConta() {
    Alert.alert(
      'Excluir conta',
      `Isso apaga permanentemente ${projetos.length} peça(s), ${materiais.length} material(is) ` +
        'e seus ajustes de precificação. Não dá para desfazer. O Google vai pedir para você ' +
        'confirmar sua identidade de novo antes de apagar.',
      [
        { text: 'Cancelar', style: 'cancel' },
        {
          text: 'Excluir tudo',
          style: 'destructive',
          onPress: async () => {
            setExcluindo(true);
            try {
              await excluirConta(apagarTudo);
              // A conta some do Firebase Auth, o observador no _layout raiz
              // detecta a sessão encerrada e troca para a tela de login.
            } catch (erro) {
              const mensagem = erro instanceof Error ? erro.message : String(erro);
              if (mensagem !== 'Reautenticação cancelada.') {
                Alert.alert('Não foi possível excluir', mensagem);
              }
            } finally {
              setExcluindo(false);
            }
          },
        },
      ],
    );
  }

  const [valorHora, setValorHora] = useState(escreverCentavos(perfil.valorHora));
  const [indiretos, setIndiretos] = useState(escreverPercentual(perfil.percIndiretos));
  const [lucro, setLucro] = useState(escreverPercentual(perfil.percLucro));
  const [taxas, setTaxas] = useState(escreverPercentual(perfil.percTaxas));
  const [arredondamento, setArredondamento] = useState<PassoArredondamento>(
    perfil.arredondamento,
  );
  const [salvo, setSalvo] = useState(false);

  const novoPerfil: PerfilPrecificacao = {
    valorHora: lerCentavos(valorHora) ?? 0,
    percIndiretos: (lerNumero(indiretos) ?? 0) / 100,
    percLucro: (lerNumero(lucro) ?? 0) / 100,
    percTaxas: (lerNumero(taxas) ?? 0) / 100,
    arredondamento,
  };

  const erros = validarPerfil(novoPerfil);

  // Prévia com uma peça imaginária de 5 h e R$ 20,00 de material, para a
  // artesã ver o efeito de mexer nos parâmetros.
  const previa = calcularPrecificacao({
    horas: 5,
    consumos: [{ quantidade: 1, custoUnitarioSnapshot: 2000 }],
    perfil: erros.length === 0 ? novoPerfil : perfil,
  });

  async function salvar() {
    if (erros.length > 0) return;
    await salvarPerfil(novoPerfil);
    setSalvo(true);
    setTimeout(() => setSalvo(false), 2000);
  }

  return (
    <Tela>
      <Cartao>
        <Rotulo>Seu trabalho</Rotulo>
        <Campo
          rotulo="Valor da hora"
          valor={valorHora}
          aoMudar={setValorHora}
          teclado="decimal-pad"
          sufixo="R$"
          dica="25,00"
        />
        <Texto variante="pequeno" cor="tinta2">
          Quanto vale uma hora do seu crochê. É o parâmetro que mais mexe no preço final.
        </Texto>
      </Cartao>

      <Cartao>
        <Rotulo>Percentuais</Rotulo>

        <View style={{ flexDirection: 'row', gap: esp.md }}>
          <View style={{ flex: 1 }}>
            <Campo
              rotulo="Indiretos"
              valor={indiretos}
              aoMudar={setIndiretos}
              teclado="decimal-pad"
              sufixo="%"
            />
          </View>
          <View style={{ flex: 1 }}>
            <Campo
              rotulo="Lucro"
              valor={lucro}
              aoMudar={setLucro}
              teclado="decimal-pad"
              sufixo="%"
            />
          </View>
          <View style={{ flex: 1 }}>
            <Campo
              rotulo="Taxas"
              valor={taxas}
              aoMudar={setTaxas}
              teclado="decimal-pad"
              sufixo="%"
            />
          </View>
        </View>

        <Texto variante="pequeno" cor="tinta2">
          Indiretos cobrem luz, agulhas, embalagem e desgaste, como percentual do custo
          direto. Taxas são o que a maquininha ou o marketplace desconta da venda.
        </Texto>

        <Selecao
          rotulo="Arredondar o preço para"
          opcoes={PASSOS_ARREDONDAMENTO}
          valor={arredondamento}
          aoMudar={setArredondamento}
          formatar={(v) => (v === 0 ? 'não arredondar' : formatarReais(v))}
        />
      </Cartao>

      {erros.map((e) => (
        <Aviso key={e} tom="erro">
          {e}
        </Aviso>
      ))}

      <Cartao>
        <Rotulo>Prévia</Rotulo>
        <Texto variante="pequeno" cor="tinta2">
          Peça de exemplo: 5 horas de trabalho e R$ 20,00 de material.
        </Texto>
        <LinhaValor rotulo="Custo total" valor={formatarReais(previa.custoTotal)} />
        <LinhaValor
          rotulo="Preço sugerido"
          valor={formatarReais(previa.precoSugerido)}
          destaque
        />
        <LinhaValor rotulo="Retorno por hora" valor={`${formatarReais(previa.retornoHora)}/h`} />
      </Cartao>

      <Botao
        titulo={salvo ? 'Salvo' : 'Salvar ajustes'}
        aoTocar={salvar}
        desabilitado={erros.length > 0}
      />

      <Cartao>
        <Rotulo>Sua conta</Rotulo>
        <Texto variante="secao">{usuario.nome ?? 'Sem nome'}</Texto>
        <Texto variante="pequeno" cor="tinta2">
          {usuario.email}
        </Texto>
        <LinhaValor rotulo="Peças cadastradas" valor={String(projetos.length)} />
        <LinhaValor rotulo="Materiais no catálogo" valor={String(materiais.length)} />
        <Texto variante="pequeno" cor="tinta3">
          Os dados ficam na sua conta Google e sincronizam entre aparelhos.
        </Texto>
        <Botao
          titulo="Sair da conta"
          variante="perigo"
          aoTocar={confirmarSaida}
          carregando={saindo}
        />
      </Cartao>

      <Cartao>
        <Rotulo>Zona de risco</Rotulo>
        <Texto variante="pequeno" cor="tinta2">
          Excluir a conta apaga suas peças, materiais e ajustes de precificação
          para sempre — não tem como desfazer.
        </Texto>
        <Botao
          titulo="Excluir conta"
          variante="perigo"
          aoTocar={confirmarExclusaoDaConta}
          carregando={excluindo}
        />
      </Cartao>
    </Tela>
  );
}
