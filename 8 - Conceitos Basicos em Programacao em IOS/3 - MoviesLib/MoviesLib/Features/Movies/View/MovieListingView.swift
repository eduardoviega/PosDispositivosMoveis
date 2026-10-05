//
//  MovieListingView.swift
//  MoviesLib
//
//  Created by Viasoft on 04/10/26.
//

import SwiftUI

struct MovieListingView: View {
    @State private var viewModel = MovieListingViewModel()
    
    var body: some View {
        Group {
            if viewModel.isLoading {
                ProgressView("Carregando filmes...")
            } else if viewModel.movies.isEmpty {
                Text("Não existem filmes cadastrados")
            } else {
                List {
                    ForEach(viewModel.movies) { movie in
                        NavigationLink(value: NavigationType.details(movie)) {
                            MovieListingRow(movie: movie)
                        }
                    }
                    .onDelete { indexSet in
                        Task {
                            await viewModel.deleteMovie(at: indexSet)
                        }
                    }
                }
            }
        }
        .task {
            await viewModel.loadMovies()
        }
        .alert("Erro", isPresented: $viewModel.hasError) {
            Button("OK") {
                viewModel.errorMessage = nil
            }
        } message: {
            Text(viewModel.errorMessage ?? "")
        }

    }
}

#Preview {
    MovieListingView()
}
