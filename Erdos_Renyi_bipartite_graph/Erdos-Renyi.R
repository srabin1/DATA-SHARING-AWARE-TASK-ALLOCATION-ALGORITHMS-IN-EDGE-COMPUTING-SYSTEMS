#install.packages("igraph")
#install.packages("ggplot2")
library(igraph)
library(ggplot2)
#library(dplyr)
options("scipen"=100, "digits"=2)

# R program to illustrate decreasing exponential distribution
# Specify x-values
x_dexp <- seq(1, 10.9, by = 0.1)
x_dexp

# y_dexp shows the value for profit 		
y_dexp <- dexp(x_dexp, rate = 10)
y_dexp

# Plot dexp values
plot(y_dexp)

# R program to illustrate increasing exponential distribution

# Specify x-values
x_pexp <- seq(1, 10.9, by = 0.1)									
x_pexp
# y_dexp shows the value for profit 
y_pexp <- pexp(x_pexp, rate = 1)
y_pexp
# Plot values				
plot(y_pexp)	

# R program to illustrate random exponential distribution

# Set seed for reproducibility
set.seed(500)

# Specify size		
N <- 100

# Draw exp distributed values
y_rexp <- rexp(N, rate = .1)
y_rexp

# Plot exp density
plot(y_rexp)
hist(y_rexp, breaks = 50, main = "")




#generate EXPONENTIAL distribution random variable for profit and request
#rexp(n, lambda) where lambda =rate and mean = 1/lambda
set.seed(1)
profit_exponential =  rexp(n = 100, rate = .1)
profit_exponential
sum(profit_exponential)
plot(profit_exponential)
hist(profit_exponential, breaks = 50, main = "")
#plotting 4 different plots near each other
par(mfrow=c(2,2))
x<-profit_exponential
#ppareto is increasing and exponential
ppareto=function(x, a=0.6, b=1) (x > b)*(1-(b/x)^a)
ppareto(x)
profit<-ppareto(x)
plot(x,ppareto(x),type="l",lty=2)
#dpareto is decreasing and exponential
dpareto=function(x, a=0.5, b=1) a*b^a/x^(a+1)
dpareto(x)
plot(x,dpareto(x),type="l",lty=2)

#generate pareto distribution random variable for profit
dpareto=function(x, a=0.5, b=1) a*b^a/x^(a+1)
ppareto=function(x, a=0.5, b=1) (x > b)*(1-(b/x)^a)
qpareto=function(u, a=0.5, b=1) b/(1-u)^(1/a)
rpareto=function(n, a=0.5, b=1) qpareto(runif(n),a,b)


par(mfrow=c(2,2))
x=seq(1,50,len=200)
x
plot(x,dpareto(x),type="l")
plot(x,ppareto(x),type="l",lty=2)
u=seq(.005,.9,len=200)
plot(u,qpareto(u),type="l",col=3)
z=rpareto(200)
dotchart(log10(z), main="200 random logged Pareto deviates",
         cex.main=.7)
par(mfrow=c(1,1))

#generate random number for profit
profit<-(runif(100, min=2, max=40))
profit
typeof(profit)
#store profit in a column
dataframe.var<-data.frame(cbind(profit))
dataframe.var

write.table(dataframe.var, sep = "," , file="C:/Users/sanaz.rabinia/proftit.csv")

#generate request: low demand
#low_request<-((runif(40, min=2, max=5))*((as.numeric(max - min)) + 1) + as.numeric(max))
#sample(1:5,40,prob=c(.01,.01,.1,.9,.9), replace=TRUE)

#######################demand ratio##########################################

#################################Constant servers: 40, case 4: 100 users##########################
low_request4<-(runif(100, min=2.0, max=4.0))
low_request4
typeof(low_request4)
sum(low_request4)

#generate request: average demand
avg_request4<-(runif(100, min=5.0, max=13.0))
avg_request4
sum(avg_request4)

#generate request: high demand
high_request4<-(runif(100, min=8.0, max=30.0))
high_request4
sum(high_request4)
################################sharing##################################
#sample_bipartite(10, 5, p=.1)
set.seed(123)
options(digits=2)
# generate random bipartite graph.
g <- sample_bipartite(10, 10, p=.2)
# check the type attribute:
V(g)$type

# define color and shape mappings.
col <- c("steelblue", "orange")
shape <- c("circle", "circle")

plot(g, layout=layout.bipartite, edge.width= 2, edge.color= "black", 
     vertex.color = col[as.numeric(V(g)$type)+1],
     vertex.shape = shape[as.numeric(V(g)$type)+1]
)

inc_matrix <- as_incidence_matrix(g)
inc_matrix
typeof(inc_matrix)
double_inc_matrix <- as.numeric(unlist(inc_matrix))
typeof(double_inc_matrix)
double_inc_matrix
write.table(inc_matrix, file="C:/Users/sanaz.rabinia/my_inc_matrix_names.txt", row.names=FALSE, col.names=FALSE)
#data_size1 <-read.table("C:/Users/sanaz.rabinia/data-size1.txt")
data_size1 <-read.csv("C:/Users/sanaz.rabinia/data-size1 - Copy.csv")
data_size1
sum(data_size1)
typeof(data_size1)
data_size1[2,2]


print(inc_matrix[,1])

i=2
data_size1$value[1]


for(j in 1:100)
{
  for (i in 1:100){
     print(inc_matrix[i,j])
     
  }
}

i=1
j=1
inc_matrix[i,j]

# if (inc_matrix[i,j]== 1){
#   inc_matrix[i,j] = data_size1$value[1]
# }


for(j in 1L:100L)
{
  for (i in 1L:100L){
    if (inc_matrix[i,j]== 1)
      inc_matrix[i,j]= data_size1$value[j]
  }
}

inc_matrix
write.table(inc_matrix, file="C:/Users/sanaz.rabinia/inc_matrix.txt", row.names=FALSE, col.names=FALSE)

inc_matrix2<-rbind(inc_matrix, c(low_request4))
write.table(inc_matrix2, file="C:/Users/sanaz.rabinia/inc_matrix2.txt", row.names=FALSE, col.names=FALSE)

inc_matrix3<-rbind(inc_matrix2, c(profit))



inc_matrix3 <- round(inc_matrix3, digits = 2)



for(j in 1L:100L)
{
  
  inc_matrix3[101,j] = paste(inc_matrix3[101,j],",",sep = "")
  inc_matrix3[102,j] = paste(inc_matrix3[102,j],",",sep = "") 
  print(inc_matrix3[102,j])
 
}

inc_matrix3 = noquote(inc_matrix3)
write.table(inc_matrix3, file="C:/Users/sanaz.rabinia/inc_matrix3.txt", row.names=FALSE, col.names=FALSE, quote = FALSE)




