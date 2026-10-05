//
//  MovieFormViewModel.swift
//  MoviesLib
//
//  Created by Viasoft on 04/10/26.
//

import Foundation
import Observation

@Observable
class MovieFormViewModel {
    var movie: Movie
    var isSaving = false
    
    private let service = MovieAPIService()
    
    init(movie: Movie? = nil) {
        self.movie = movie ?? Movie()
    }
    
    func saveMovie() async throws {
        isSaving = true
        
        defer { isSaving = false }
        
        do {
            if movie.id == nil {
                _ = try await service.createMovie(movie)
            } else {
                _ = try await service.updateMovie(movie)
            }
        } catch {
            throw error
        }
    }
}
