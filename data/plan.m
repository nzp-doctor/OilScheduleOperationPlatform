%机器 任务 时间1 时间2  属性
a={1   '#3 (25,000)'  0  100   3;
    1   '#4 (24,000)'  100  196   4;
    1   '#8 (24,000)'  196  372   8;
    2   '#1 (16,000)'  0  48   1;
    2   '#2 (26,000):'  48  126   2;
    2   '#11 (82,300)'  126  372   11;
    3   '#5 (15,000)'  0  60   5;
    3   '#4 (17,000)'  60  128   4;
    3   '#6 (18,000)'  128  200   6;
    3   '#10 (43,055)'  200  372   10;
  4   '#7 (22,000)'  0  35   7;
  4   '#8 (103,000)'  35  200   8;
  4   '#9 (107,638)'  200  372   9;};
ylabels = string({"DS1","DS2","DS3","DS4"});%num2str((1:max(a(:,1)))','DS%d');

figure(1);
clf;
w=0.5;      
set(gcf,'color','w');

%配色方案
colors = {[51,204,255]/255;
[255,255,0]/255;
[51,204,102]/255;
[51,255,204]/255;
[255,255,153]/255;
[219,186,119]/255;

[204,255,255]/255;
[102,255,51]/255;
[255,204,0]/255;
[102,153,255]/255;
[153,204,51]/255;};

for ii=1:size(a,1)
   % 利用颜色区分所装的原油类型
   x=cell2mat({a{ii,[3 3 4 4]}});   
   y=a{ii,1}+[-w/2 w/2 w/2 -w/2];
   p=patch('xdata',x,'ydata',y,'facecolor',colors{a{ii,5}},'LineWidth',1.5);   
   text(a{ii,3}+0.5,a{ii,1},a{ii,2},'FontSize',9);
end
xlabel('Time Horizon(h)');
ylabel('Operation');
set(gca,'Box','on');
set(gca,'YTick',0:max(cell2mat({a{:,1}})));
set(gca,'YTickLabel',{'';ylabels;''}); 

% 设置宽高
set(gcf,'position',[500,300,1000,250]);
% 设置坐标轴字体大小
set(gca,'FontName','Times New Roman','FontSize',14,'LineWidth',1.5);
