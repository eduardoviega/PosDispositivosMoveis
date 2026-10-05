//
//  MovieFormView.swift
//  MoviesLib
//
//  Created by Viasoft on 04/10/26.
//

import SwiftUI

import SwiftUI

struct MovieFormView: View {
    @Environment(NavigationRouter.self) private var router
    
    @State private var viewModel: MovieFormViewModel
    
    init(movie: Movie? = nil) {
        _viewModel = State(initialValue: MovieFormViewModel(movie: movie))
    }
    
    var body: some View {
        VStack {
            form
            saveButton
        }
        .navigationTitle(viewModel.movie.id == nil ? "Cadastro" : "Edição")
        .toolbar {
            Button("Voltar p/ início") {
                router.path.removeLast(router.path.count)
            }
        }
    }
    
    private var saveButton: some View {
        Button {
            Task {
                try await viewModel.saveMovie()
                router.path.removeLast()
            }
        } label: {
            Text(viewModel.movie.id == nil ? "Cadastrar filme" : "Salvar alterações")
                .padding(.vertical, 6)
                .frame(maxWidth: .infinity)
        }
        .buttonStyle(.borderedProminent)
        .padding(16)
        .disabled(viewModel.isSaving)
    }
    
    private var form: some View {
        Form {
            Section("Título") {
                TextField("Escreva o nome do filme", text: $viewModel.movie.title)
            }
            
            Section("Nota e duração") {
                HStack {
                    TextField("Nota", value: $viewModel.movie.rating, format: .number)
                    TextField("Duração", text: $viewModel.movie.duration)
                }
            }
            
            Section("Categorias") {
                TextField("Insira as principais categorias", text: $viewModel.movie.categories)
            }

            Section("Sinopse") {
                TextEditor(text: $viewModel.movie.synopsis)
                    .frame(height: 120)
            }
            
            Section("Pôster") {
                TextField("URL do pôster", text: $viewModel.movie.poster)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled()
            }
            
            Section("Trailer") {
                TextField("URL do trailer", text: trailer)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled()
            }
        }
    }
    
    private var trailer: Binding<String> {
        Binding<String>(
            get: {
                viewModel.movie.trailer ?? ""
            },
            set: { newValue in
                viewModel.movie.trailer = newValue
            }
        )
    }
}

#Preview {
    MovieFormView()
        .environment(NavigationRouter())
}
