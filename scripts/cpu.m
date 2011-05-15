load ../dumps/cpu.dump;
y = cpu(:,1);
tp = cpu(:,2);
cpuk = cpu(:,3);
nn = cpu(:,4);

X = [tp cpuk nn];
b = regress(y,X);

scatter3(tp,nn,y,'filled')
hold on
xlabel('Throughput')
ylabel('Number of nodes')
zlabel('Average CPU')
view(50,10)