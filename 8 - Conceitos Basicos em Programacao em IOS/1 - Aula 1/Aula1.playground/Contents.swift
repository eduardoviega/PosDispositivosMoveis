import UIKit

var greeting = "Hello, playground"


/// Método para realizar uma requisição HTTP
/// - Parameters:
///   - url: URL(Universal Resource Location) contendo a rota a ser executada
///   - method: Método HTTP a ser usado (POST, GET, etc.). Padrão "GET"
///   - parameters: Dicionário contendo a lista de parâmetros enviados no corpo da requisição
///   - header: Dicionário contendo a lista de parâmetros enviados no cabeçalho da requisição
/// - Returns: Não há retorno
func request(
    _ url: URL,
    method: String = "GET",
    parameters: [String: Any]? = nil,
    header: [String: Any]? = nil) -> Void {
        
}


// Variáveis

// Inferência de tipo
var acconutValue = -350.0
acconutValue = 10000.00

// Explícito
var age: Int = 21
age = 22

// Fortemente tipada
var x = 22, y = 33, z = 44
//x = "Olá"

// Character
var a: Character = "a"
//a = "oi"
var letter = ""

// Constantes (valores imutáveis)
let cpf = "123.456.789-00"
//cpf = "121212"


// String, Character, Bool, Int, Double, Float

// String
var sentence: String = "Olá!!"

let firstName: String = "Eduardo"
let lastName: String = "Viega"
// let greetings = "Sou o " + firstName + " " + lastName + ", e tenho " + age + " anos."

// Interpolação de String
let greetings = "Sou o \(firstName) \(lastName) e tenho \(age) anos."

var text: String = """
sdgfdsaf
sdfasd  adsf tbt5 tth rthr
 erg e   rgrgdf
"""
//print(text)

// Tuple (Tupla)
//let address = "Rua das Flores, 123, 9.8765, -12.98765"
let address: (street: String, number: Int, lat: Double, long: Double) = ("Rua das Flores", 123, 9.8765, -12.98765)
//print("Eu moro na \(address.street), número \(address.number), cuja latitude é \(address.lat)")


// Decomposição de Tuplas
let (_, _, _, longitutde) = address

//print(longitutde)


// EOP (Emoji Oriented Programming)
let 🐶 = "Bud"
let 🐩 = "Pet"
let 💩 = "cocô"
let dogSentence = "O \(🐶) e o \(🐩) vivem fazendo \(💩)"

//print(dogSentence)

// Optional
var driverLicense: String?
//driverLicense = "abc9999"

print("A sua carteira de motorista é \(driverLicense)")


// Unwrap
// JEITO VIDA LOKA
//print("A sua carteira de motorista é \(driverLicense!)")

// Optional binding
if let driverLicense {
    print(">>>", driverLicense)
} else {
    print("Está vazio o Optional")
}
print(">>>", driverLicense)

if driverLicense != nil {
    print(">>>", driverLicense)
}

// Operador de coalecência nula (Nil Coalescing Operator)
// ??

let number = Int("zzzz")
let number2 = Int("5")

let finalNumber = number ?? number2 ?? 0
print(finalNumber)

let uppercasedDriverLicense = driverLicense?.uppercased()
print(uppercasedDriverLicense)


func something() {
    var driverLicense: String? = "ZZZ"
    
    guard let driverLicense else {
        print("Você não tem")
        return
    }
    
    print(">>", driverLicense)
    print(">>", driverLicense)
    print(">>", driverLicense)
    print(">>", driverLicense)
}

something()


// Coleções
// Array, Dictionary, Set

// Array: Coloção ordenada de valores de mesmo tipo
var emptyArray: [String] = []
//var emptyArray2: Array<String> = []
//var emptyArray3: [(String, Int)] = []
//var emptyArray4 = [String]()


var shoppingList = ["Açucar", "Leite", "Café"]

emptyArray.isEmpty // false, true
emptyArray.count

if shoppingList.isEmpty {
    print("sua lista de compras está vazia")
}

// Adicionar
shoppingList.append("Sabão")
shoppingList += ["Uva", "Banana"]

// Remover
//shoppingList.removeLast(2)
//shoppingList

if shoppingList.count > 4 {
    shoppingList.remove(at: 4)
}

print(shoppingList[1])

shoppingList.append("Pera")
if !shoppingList.contains("Pera") {
    print("Você precisa comprar pera")
}

if "Olá mundo".contains("mundo") {
    print("SIM")
}

"Pera" != "pera"

shoppingList[0] = "Rabanada"
shoppingList

// PascalCase e camelCase
// Objetos, parâmetros, métodos, função -> camelCase
// Tipos, Classes, Estruturas, Protocolos, Enumeradores -> PascalCase


// Dicionários (Dictionary)
// Lista NÃO ordenada de valores de mesmo tipo, acessíveis por uma chave
var states: [String: String] = [
    "SP": "São Paulo",
    "MG": "Minas Gerais",
    "RJ": "Rio de Janeiro",
    "PA": "Pará",
    "BA": ""
]
var emptyDictionary: [Int: String] = [:]

states.count
states.isEmpty

states["RJ"]
if let rj = states["RJ"] {
    print(rj)
}

// Adicionar
states["SC"] = "Santa Catarina"

// Remover
// states["BA"] = nil

states["BA"] = "Bahia"
states


// Set
// Coleção não ordenada de valores ÚNICOS de mesmo tipo
var movies: Set<String> = ["Matrix", "Vingadores", "Jurassic Park"]
var myWifeMovies: Set<String> = ["Homem-Aranha", "Vingadores", "Jurassic Park", "De Volta para o futuro"]

movies.isEmpty
movies.count
movies.contains("Vingadores")

movies.insert("Substância")
movies.insert("Substância")

movies.remove("Substância")

let allMovies = movies.union(myWifeMovies)
allMovies

let favoriteMovies = movies.intersection(myWifeMovies)
favoriteMovies

movies = movies.subtracting(myWifeMovies)
movies


// Condicionais
// If else
let grade = 5.0
if grade > 6.0 {
    print("Passou")
} else {
    print("Reprovou")
}

var temperature = 19, climate = ""
if temperature <= 0 {
    climate = "Gelado"
} else if temperature < 14 {
    climate = "Frio"
} else if temperature < 21 {
    climate = "Agradável"
} else {
    climate = "Quente"
}

// Switch
var newLetter = "f", letterType = ""
switch newLetter {
case "a":
    letterType = "vogal"
case "e":
    letterType = "vogal"
case "i":
    letterType = "vogal"
case "o":
    letterType = "vogal"
case "u":
    letterType = "vogal"
default:
    letterType = "consoante"
}

// ..< // Half-closed range
// ... // Open range

let speed = 17.0
switch speed {
case 0..<20:
    print("Primeira marcha")
case 20..<40:
    print("Segunda marcha")
case 40..<60:
    print("Terceira marcha")
default:
    print("Quarta marcha")
}

switch newLetter {
case "a"..."m":
    print("Está na primeira metade do alfabeto")
default:
    print("Está na segunda metade do alfabeto")
}

// While
//var count = 1
//while count <= 15 {
//    print("Olá")
//}

// For in
//for number in 0...100 {
//    print(number)
//}
//
//for product in shoppingList {
//    print(product)
//}

//for (_, name) in states {
//    print(name)
//}

// Enumeradores
enum Compass {
    case north
    case south
    case east
    case west
}

var heading: Compass = .south

//switch heading {
//case .north:
//    print("Indo para o norte")
//case .east:
//    print("Indo para a leste")
//case .west:
//    print("Indo para a oeste")
//case .south:
//    print("Indo para o sul")
//}


// Valor padrão
enum Weekday: String {
    case sunday = "domingo"
    case monday
    case tuesday
    case wednesday
    case thursday
    case friday
    case saturday = "sábado"
}

let today: Weekday? = Weekday(rawValue: "domingo")
print("Hoje é", today?.rawValue ?? "dia desconhecido")


// Valor Associado
enum Measure {
    case age(Int)
    case weight(Double)
    case size(weight: Double, height: Double)
    
    // Propriedade computada
    var message: String {
        switch self {
            case .age(let age):
            return "Idade \(age)"
        case .weight(let weight):
            return "Peso \(weight)"
        case .size(weight: let weight, height: let height):
            return "Altura \(weight)x\(height)cm"
        }
    }
}

let measure: Measure = .weight(75)

print(measure.message)


//switch measure {
//    case .age(let age):
//    print("Idade \(age)")
//case .weight(let weight):
//    print("Peso \(weight)")
//case .size(weight: let weight, height: let height):
//    print("Altura \(weight)x\(height)cm")
//}

// Funções
func doNothing() {
    // Código da função
}
doNothing()

func double(value: Int) -> Int {
    return value * 2
}
double(value: 4)

func say(_ sentence: String, to person: String) {
    print(sentence, person)
}
say("Olá", to: "Eric")

// Eduardo Viega
//say(sentence: "Olá", person: "Eric")

//say("Olá", to: "Eric")
// Diga Olá para Eric


// Closure
func sum(_ number1: Int, _ number2: Int) -> Int {
    return number1 + number2
}
func multiply(_ number1: Int, _ number2: Int) -> Int {
    return number1 * number2
}
func divide(_ number1: Int, _ number2: Int) -> Int {
    return number1 / number2
}
func subtract(_ number1: Int, _ number2: Int) -> Int {
    return number1 - number2
}

typealias FuncOperation = (Int, Int) -> (Int)

// Fist Class Citizen
// Mais branca que existe
func calculate(
    _ number1: Int,
    _ number2: Int,
    using operation: FuncOperation) -> Int {
    operation(number1, number2)
}

calculate(20, 4, using: divide)


func getOperation(named: String) -> FuncOperation {
    switch named.lowercased() {
    case "soma":
        return sum
    case "subtração":
        return subtract
    case "divisão":
        return divide
    default:
        return multiply
    }
}

let sumOperation = getOperation(named: "soma")
sumOperation(9, 12)


// Closure

// Sintaxe Função
/*
 func nome(nomeParam1: TipoParam1, nomeParam2: TipoParam2) -> TipoRetorno {
     // Código
     return algoTipoRetorno
 }
 */

// Sintaxe Closure
/*
 {(nomeParam1: TipoParam1, nomeParam2: TipoParam2) -> TipoRetorno in
     // Código
     return algoTipoRetorno
 }
*/

calculate(37, 5, using: {(number1: Int, number2: Int) -> Int in
    return number1 % number2
})

calculate(37, 5, using: {(number1, number2) -> Int in
    return number1 % number2
})

calculate(37, 5, using: {x, y in
    return x % y
})

calculate(37, 5, using: {
    return $0 % $1
})

calculate(37, 5, using: { $0 % $1 })

// High Order Function
calculate(37, 5){ $0 % $1 }


let names = ["Eric", "Ana", "Bia", "Pedro", "Daniel", "Eduardo"]

//var upperCasedNames: [String] = []
//for name in names {
//    upperCasedNames.append(name.uppercased())
//}
//print(upperCasedNames)

let upperCasedNames = names.map{$0.uppercased()}
upperCasedNames

let fiveLettersNames = names.filter{$0.count == 5}
fiveLettersNames


// Classes vs Structs
// Classe: Reference Type
// Passados via referência
class Person {
    let name: String
    var age: Int
    weak var friend: Person?
    
    init(name: String, age: Int) {
        self.name = name
        self.age = age
    }
    
    func run() {
        
    }
    
    deinit {
        print(name, "morreu")
    }
}

var felipe: Person? = Person(name: "Felipe", age: 45)
var paulo: Person? = Person(name: "Paulo", age: 37)

felipe?.friend = paulo
paulo?.friend = felipe

//var joao = felipe
//
//print(felipe.age)
//print(joao.age)
//
//felipe.age += 1
//print(felipe.age)
//print(joao.age)

// ARC: Automatic Reference Counting
felipe?.friend = nil
paulo?.friend = nil

felipe = nil
paulo = nil



class Temperature {
    var celcius: Double
    
    init(celcius: Double) {
        self.celcius = celcius
    }
    
    // Propriedade Computada
    var fahrenheit: Double {
        get {
            celcius * 9/5 + 32
        }
        set {
            celcius = (newValue - 32) * 5/9
        }
    }
}

var todaysTemperature = Temperature(celcius: 25)

print(todaysTemperature.celcius)
print(todaysTemperature.fahrenheit)


//todaysTemperature.celcius = 13
todaysTemperature.fahrenheit = 90

print(todaysTemperature.celcius)
print(todaysTemperature.fahrenheit)



// Struct: Value Type
// Passados via cópia
struct Person2 {
    // Propriedades armazenadas
    let name: String
    var age: Int
    
    // Memberwise Initializer
    func run() {
        
    }
}

var felipe2: Person = Person(name: "Felipe", age: 45)
var paulo2: Person = Person(name: "Paulo", age: 37)
var joao2 = felipe2

print(felipe2.age)
print(joao2.age)

felipe2.age += 1
print(felipe2.age)
print(joao2.age)



// Extension
var name = "ulisses tiago fernando paulo raia"

extension String {
    var initials: String {
        self.capitalized
            .components(separatedBy: " ")
            .map{String($0.first ??  Character(""))}
            .joined()
    }
}
print(name.initials)
