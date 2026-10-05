//
//  MovieListingRow.swift
//  MoviesLib
//
//  Created by Viasoft on 04/10/26.
//

import SwiftUI

struct MovieListingRow: View {
    let movie: Movie
    
    var body: some View {
        HStack(spacing: 12) {
            MoviePoster(posterPath: movie.poster)
                .frame(width: 50, height: 80)
                .clipped()
                .cornerRadius(8)
                .shadow(radius: 4, x: 2, y: 2)
            
            Text(movie.title)
            
            Spacer()
            
            Text(movie.finalRating)
        }
            
    }
}

#Preview {
    MovieListingRow(movie: Movie(title: "Meu filme", rating: 9.0, poster: "https://acdn-us.mitiendanube.com/stores/004/687/740/products/pos-00903-641b31af291835791717181354202331-640-0.webp"))
}
