load ../dumps/tc.dump;
y = tc(:,1);
tp = tc(:,2);
bw = tc(:,3);
tck = tc(:,4);
nn = tc(:,5);

X = [tp bw tck nn];
b = regress(y,X);

scatter3(tp,nn,y,'filled')
hold on
xlabel('Throughput')
ylabel('Number of nodes')
zlabel('Average CPU')
view(50,10)