//
//  Movie.swift
//  MoviesLib
//
//  Created by Viasoft on 04/10/26.
//

class Movie: Identifiable, Codable, Equatable, Hashable {
    var id: Int?
    var title: String
    var categories: String
    var duration: String
    var rating: Double
    var synopsis: String
    var poster: String
    var trailer: String?
    
    var finalRating: String { "\(rating)/10" }
    var hasTrailer: Bool { trailer?.isEmpty == false }
    
    init(
        id: Int? = nil,
        title: String = "",
        categories: String = "",
        duration: String = "",
        rating: Double = 0,
        synopsis: String = "",
        poster: String = "",
        trailer: String? = nil
    ) {
        self.id = id
        self.title = title
        self.categories = categories
        self.duration = duration
        self.rating = rating
        self.synopsis = synopsis
        self.poster = poster
        self.trailer = trailer
    }
    
    static func == (lhs: Movie, rhs: Movie) -> Bool {
        lhs.id == rhs.id &&
        lhs.title == rhs.title &&
        lhs.categories == rhs.categories &&
        lhs.duration == rhs.duration &&
        lhs.rating == rhs.rating &&
        lhs.synopsis == rhs.synopsis &&
        lhs.poster == rhs.poster &&
        lhs.trailer == rhs.trailer
    }
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
        hasher.combine(title)
        hasher.combine(categories)
        hasher.combine(duration)
        hasher.combine(rating)
        hasher.combine(synopsis)
        hasher.combine(poster)
        hasher.combine(trailer)
    }
}
