//
//  ListView.swift
//  IMC
//
//  Created by Viasoft on 03/10/26.
//

import SwiftUI

struct ListView: View {
    private let imcStorage = IMCStorage()
    
    var body: some View {
        Group {
            if imcStorage.items.isEmpty {
                emptyContent
            } else {
                content
            }
        }
        .navigationTitle("Lista de IMCs")
    }
    
    var content: some View {
        List {
            ForEach(imcStorage.items) { item in
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(item.name)
                            .font(.headline)
                        Text(item.gender)
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                    Spacer()
                    Text(String(format: "%.2f", item.imc))
                        .font(.title3)
                        .fontWeight(.semibold)
                        .foregroundStyle(.accent)
                }
            }
            .onDelete(perform: { indexSet in
                for index in indexSet {
                    imcStorage.items.remove(at: index)
                }
            })
            .padding(.vertical, 4)
        }
    }
    
    var emptyContent: some View {
        VStack {
            Image("EmptyList")
                .resizable()
                .scaledToFit()
                .frame(width: 220)
            
            Text("Nenhum IMC cadastrado")
                .font(.title3)
                .foregroundStyle(.secondary)

        }
    }
}

#Preview {
    ListView()
}
