# This R script reads data.csv, cleans and plots the data. Outputing data.png.
# 
# data.csv must the output timings of an algorithm for different input sizes.
# Each row reprisenting different sizes, and each column reprisenting 
#
# The script with take the average of each row, while removing any outliers.

isBetween <- function(x, min, max) {
  if (x >= min && x <= max) return(TRUE) else return(FALSE)
}

isBetween = Vectorize(isBetween);
df <- read.csv('data.csv');
df <- t(df);
v <- c();

for (col in 1:ncol(df)) {
  l <- as.vector(unlist(df[,col][-1]));
  iqr <- IQR(df[,col][-1]);
  q1 <- unlist(quantile(l)[2]);
  q2 <- unlist(quantile(l)[4]);

  v <- append(v,mean(l[isBetween(l,q1-iqr,q2+iqr)]));
}

png("data.png");
plot(v);
dev.off();
