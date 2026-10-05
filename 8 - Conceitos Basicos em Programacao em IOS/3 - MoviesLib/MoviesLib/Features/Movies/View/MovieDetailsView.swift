//
//  MovieDetailsView.swift
//  MoviesLib
//
//  Created by Viasoft on 04/10/26.
//

import AVKit
import SwiftUI

struct MovieDetailsView: View {
    @Environment(NavigationRouter.self) private var router
    
    @State private var viewModel: MovieDetailViewModel
    @State private var scale: Double = 0
    @State private var cornerRadius: Double = 200
    @State private var degrees: Double = 0
    
    init(movie: Movie) {
        _viewModel = State(initialValue: MovieDetailViewModel(movie: movie))
    }
    
    var body: some View {
        ScrollView {
            VStack {
                ZStack {
                    poster
                    if let player = viewModel.player {
                        VideoPlayer(player: player)
                            .frame(width: 400, height: 400)
                            .clipShape(RoundedRectangle(cornerRadius: cornerRadius))
                            .scaleEffect(scale)
                            .rotationEffect(.degrees(degrees))
                    }
                }
                VStack(alignment: .leading, spacing: 10) {
                    title
                    rating
                    categories
                    playButton
                    synopsis
                }
                .padding(.horizontal)
            }
        }
        .ignoresSafeArea(edges: .top)
        .onAppear {
            viewModel.setupPlayer()
        }
        .toolbar {
            Button("Editar") {
                router.path.append(NavigationType.form(viewModel.movie))
            }
        }
    }
    
    private var poster: some View {
        MoviePoster(posterPath: viewModel.movie.poster)
            .frame(height: 400)
            .clipped()
            .mask {
                LinearGradient(
                    stops: [
                        .init(color: .black, location: 0.75),
                        .init(color: .clear, location: 1)
                    ],
                    startPoint: .top,
                    endPoint: .bottom
                    
                )
            }
        
    }
    
    private var title: some View {
        Text(viewModel.movie.title)
            .fontWeight(.bold)
            .font(.title)
            .multilineTextAlignment(.leading)
    }
    
    private var rating: some View {
        HStack {
            Image(systemName: "star.fill")
                .foregroundColor(.yellow)
            
            Text(viewModel.movie.finalRating)
            
            Spacer()
        }
    }
    
    private var categories: some View {
        Text(viewModel.movie.categories)
    }
    
    private var playButton: some View {
        Button {
            viewModel.togglePlayer()
            withAnimation(.bouncy(duration: 1.5)) {
                scale = 1
                cornerRadius = 0
                degrees = 360
            }
        } label: {
            HStack {
                Image(systemName: !viewModel.movie.hasTrailer ? "xmark" : viewModel.isPlaying ? "stop.fill" : "play.fill")
                    .padding(10)
                    .background(Color.white)
                    .cornerRadius(60)
                
                Text(!viewModel.movie.hasTrailer ? "Sem trailer" : "Trailer")
                    .fontWeight(.semibold)
                    .padding(.trailing)
                    .foregroundColor(.primary)
            }
            .padding(3)
            .background(Color.gray.opacity(0.2))
            .cornerRadius(40)
        }
        .disabled(!viewModel.movie.hasTrailer)
    }
    
    private var synopsis: some View {
        VStack(alignment: .leading) {
            Text("Sinopse")
                .padding(.top)
                .font(.title3)
                .fontWeight(.semibold)
                .padding(.horizontal)

            ScrollView {
                Text(viewModel.movie.synopsis)
            }
            .padding(.horizontal)
            .padding(.bottom)
            .frame(height: 120)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.gray.opacity(0.2))
        .cornerRadius(8)
        .padding(.vertical)
    }
}

#Preview {
    MovieDetailsView(movie: Movie(
        title: "Forma da Água",
        categories: "Ação, terror, suspense",
        duration: "2h 28min",
        rating: 10,
        synopsis: "Um hacker aprende com os misteriosos rebeldes sobre a verdadeira natureza de sua realidade e seu papel na guerra contra seus controladores. Um hacker aprende com os misteriosos rebeldes sobre a verdadeira natureza de sua realidade e seu papel na guerra contra seus controladores. Um hacker aprende com os misteriosos rebeldes sobre a verdadeira natureza de sua realidade e seu papel na guerra contra seus controladores.",
        poster: "https://m.media-amazon.com/images/I/A1ZXHdyJQUL._UF1000,1000_QL80_.jpg",
        trailer: "https://archive.org/download/the-shape-of-water-2017-720p-trailer/The-Shape-of-Water_2017_720p_trailer.mp4"
    )
    )
    .environment(NavigationRouter())
}
