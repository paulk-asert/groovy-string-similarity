import static org.codehaus.groovy.util.StringUtil.bar

def results = [
    angle:[
cow: [bovine:0.86, cattle:0.83, calf:0.80, milk:0.75, bull:0.73],
bull: [bear:0.74, cow:0.73, cattle:0.71, bovine:0.70, calf:0.68],
calf: [bovine:0.81, cow:0.80, cattle:0.78, bear:0.69, bull:0.68],
bovine: [cow:0.86, cattle:0.85, calf:0.81, livestock:0.75, bull:0.70],
cattle: [bovine:0.85, livestock:0.84, cow:0.83, calf:0.78, bull:0.71],
livestock: [cattle:0.84, bovine:0.75, cow:0.71, bull:0.67, calf:0.66],
cat: [feline:0.92, kitten:0.82, bear:0.70, milk:0.63, bull:0.63],
kitten: [feline:0.82, cat:0.82, bear:0.62, milk:0.56, bovine:0.56],
feline: [cat:0.92, kitten:0.82, bear:0.67, bovine:0.62, bull:0.60],
hippo: [bull:0.61, calf:0.61, bear:0.61, bovine:0.59, cat:0.59],
bear: [bull:0.74, cat:0.70, bovine:0.69, calf:0.69, milk:0.69],
bare: [bear:0.59, milk:0.57, green:0.56, grass:0.56, water:0.54],
milk: [cow:0.75, bear:0.69, bovine:0.69, cattle:0.68, water:0.67],
water: [milk:0.67, green:0.66, grass:0.65, cat:0.60, bear:0.59],
grass: [green:0.74, water:0.65, cat:0.59, livestock:0.58, cattle:0.57],
green: [grass:0.74, water:0.66, cat:0.62, bear:0.62, feline:0.59]
    ],
    use:[
cow: [bovine:0.72, cattle:0.71, calf:0.65, livestock:0.61, bull:0.57],
bull: [cow:0.57, cattle:0.53, bovine:0.51, livestock:0.47, calf:0.45],
calf: [cow:0.65, cattle:0.62, bovine:0.58, livestock:0.56, bull:0.45],
bovine: [cattle:0.74, cow:0.72, livestock:0.63, calf:0.58, bull:0.51],
cattle: [livestock:0.84, bovine:0.74, cow:0.71, calf:0.62, bull:0.53],
livestock: [cattle:0.84, bovine:0.63, cow:0.61, calf:0.56, bull:0.47],
cat: [kitten:0.75, feline:0.74, bear:0.45, cow:0.44, grass:0.37],
kitten: [cat:0.75, feline:0.69, cow:0.44, milk:0.42, bear:0.38],
feline: [cat:0.74, kitten:0.69, cow:0.38, livestock:0.37, grass:0.37],
hippo: [cow:0.36, feline:0.35, bull:0.34, bear:0.34, calf:0.33],
bear: [bare:0.52, cat:0.45, cow:0.41, bull:0.41, kitten:0.38],
bare: [bear:0.52, calf:0.37, cat:0.32, grass:0.32, green:0.31],
milk: [cow:0.56, water:0.54, calf:0.44, kitten:0.42, bovine:0.41],
water: [milk:0.54, grass:0.46, green:0.38, cow:0.37, cat:0.37],
grass: [cow:0.48, water:0.46, livestock:0.42, calf:0.41, cattle:0.40],
green: [grass:0.39, water:0.38, cat:0.36, bear:0.33, bare:0.31]
    ],
    conceptnet:[
cow: [bovine:0.77,cattle:0.77,livestock:0.63,bull:0.54,calf:0.53],
bull: [cow:0.54,cattle:0.50,bovine:0.46,livestock:0.37,calf:0.35],
calf: [cow:0.53,bovine:0.50,cattle:0.48,livestock:0.39,bull:0.35],
bovine: [cow:0.77,cattle:0.75,livestock:0.62,calf:0.50,bull:0.46],
cattle: [livestock:0.89,cow:0.77,bovine:0.75,bull:0.50,calf:0.48],
livestock: [cattle:0.89,cow:0.63,bovine:0.62,calf:0.39,bull:0.37],
cat: [kitten:0.84,feline:0.84,bull:0.25,bear:0.21,cow:0.20],
kitten: [cat:0.84,feline:0.71,bear:0.24,bull:0.20,hippo:0.20],
feline: [cat:0.84,kitten:0.71,bear:0.24,hippo:0.24,livestock:0.24],
hippo: [cow:0.32,bovine:0.31,bear:0.28,calf:0.25,bull:0.25],
bear: [bull:0.32,hippo:0.28,feline:0.24,kitten:0.24,cat:0.21],
bare: [grass:0.11,bear:0.10,kitten:0.07,cat:0.03,cattle:0.03],
milk: [cow:0.50,bovine:0.38,cattle:0.36,livestock:0.32,water:0.29],
water: [milk:0.29,hippo:0.20,grass:0.19,green:0.14,livestock:0.09],
grass: [green:0.34,livestock:0.23,water:0.19,cattle:0.18,calf:0.12],
green: [grass:0.34,water:0.14,hippo:0.13,milk:0.05,bear:0.04],
],
    glove:[
cow: [bovine:0.67,cattle:0.62,milk:0.48,livestock:0.47,calf:0.44],
bull: [cow:0.38,bear:0.38,calf:0.32,cattle:0.32,cat:0.30],
calf: [cow:0.44,bovine:0.34,bull:0.32,cattle:0.30,hippo:0.28],
bovine: [cow:0.67,cattle:0.42,feline:0.34,calf:0.34,milk:0.28],
cattle: [livestock:0.78,cow:0.62,bovine:0.42,milk:0.38,bull:0.32],
livestock: [cattle:0.78,cow:0.47,milk:0.33,water:0.29,bovine:0.28],
cat: [feline:0.49,kitten:0.43,cow:0.34,bear:0.33,bull:0.30],
kitten: [cat:0.43,feline:0.38,hippo:0.25,cow:0.22,calf:0.20],
feline: [cat:0.49,kitten:0.38,bovine:0.34,hippo:0.26,cow:0.16],
hippo: [calf:0.28,feline:0.26,kitten:0.25,bull:0.21,cow:0.20],
bear: [bull:0.38,cat:0.33,cow:0.27,livestock:0.21,cattle:0.21],
bare: [grass:0.27,water:0.22,green:0.16,calf:0.13,cat:0.13],
milk: [cow:0.48,water:0.42,cattle:0.38,livestock:0.33,bovine:0.28],
water: [milk:0.42,green:0.35,grass:0.29,livestock:0.29,bare:0.22],
grass: [green:0.36,water:0.29,bare:0.27,livestock:0.25,cattle:0.24],
green: [grass:0.36,water:0.35,milk:0.21,bear:0.17,cow:0.16],
],
    fasttext:[
cow: [bovine:0.72,cattle:0.70,calf:0.67,bull:0.67,milk:0.66],
bull: [cow:0.67,cattle:0.59,bovine:0.57,calf:0.56,bear:0.50],
calf: [cow:0.67,cattle:0.59,bull:0.56,bovine:0.54,livestock:0.52],
bovine: [cow:0.72,cattle:0.61,bull:0.57,calf:0.54,livestock:0.52],
cattle: [livestock:0.85,cow:0.70,bovine:0.61,calf:0.59,bull:0.59],
livestock: [cattle:0.85,cow:0.61,water:0.52,bovine:0.52,calf:0.52],
cat: [kitten:0.75,feline:0.74,cow:0.45,hippo:0.44,bull:0.40],
kitten: [cat:0.75,feline:0.67,hippo:0.48,cow:0.48,calf:0.46],
feline: [cat:0.74,kitten:0.67,bovine:0.48,hippo:0.46,cow:0.41],
hippo: [cow:0.49,calf:0.49,kitten:0.48,feline:0.46,cat:0.44],
bear: [bull:0.50,bare:0.49,kitten:0.41,calf:0.41,livestock:0.41],
bare: [bear:0.49,grass:0.46,green:0.41,bull:0.37,water:0.35],
milk: [cow:0.66,water:0.57,cattle:0.53,livestock:0.51,calf:0.50],
water: [milk:0.57,grass:0.52,livestock:0.52,green:0.48,cattle:0.43],
grass: [green:0.57,water:0.52,livestock:0.48,cow:0.48,cattle:0.47],
green: [grass:0.57,water:0.48,bare:0.41,cow:0.39,bear:0.36],
]]

def W = 20
def algs = results.keySet()
def words = results[algs[0]].keySet()
println 'Algorithm'.padRight(W) + algs.collect{ it.padRight(W) }.join(' ')
words.each { wd ->
    (0..4).each {n ->
        if (n == 0) println()
        print (( n == 2 ? wd : '').padRight(W))
        algs.each { a -> print results[a][wd].entrySet()[n].collect{int v = it.value * 10; "${it.key.padRight(10)}${bar(v, 0, 10, 10)}${' '.padRight(10 - v)}" }.join() }
        println()
    }
}
