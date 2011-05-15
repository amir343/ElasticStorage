load ../dumps/bw.dump;
y = bw(:,1);
tp = bw(:,2);
bwk = bw(:,3);
nn = bw(:,4);

X = [tp bwk nn];
b = regress(y,X);

scatter3(tp,nn,y,'filled')
hold on
xlabel('Throughput')
ylabel('Number of nodes')
zlabel('Average CPU')
view(50,10)