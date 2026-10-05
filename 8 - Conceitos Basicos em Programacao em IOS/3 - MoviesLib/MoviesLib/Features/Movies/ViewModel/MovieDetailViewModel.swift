//
//  MovieDetailViewModel.swift
//  MoviesLib
//
//  Created by Viasoft on 04/10/26.
//
import AVKit
import Observation

@Observable
class MovieDetailViewModel {
    let movie: Movie
    var player: AVPlayer?
    var isPlaying: Bool = false
    
    init(movie: Movie) {
        self.movie = movie
    }
    
    func setupPlayer() {
        if let trailer = movie.trailer, let url = URL(string: trailer) {
            player = AVPlayer(url: url)
        }
    }
    
    func togglePlayer() {
        if isPlaying {
            player?.pause()
        } else {
            player?.play()
        }
        isPlaying.toggle()
    }
    
    func cleanUp() {
        player?.pause()
        player = nil
        isPlaying = false
    }
    
}
